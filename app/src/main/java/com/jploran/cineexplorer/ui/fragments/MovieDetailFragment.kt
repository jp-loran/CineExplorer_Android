package com.jploran.cineexplorer.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.jploran.cineexplorer.R
import com.jploran.cineexplorer.data.remote.model.MovieDto
import com.jploran.cineexplorer.databinding.FragmentMovieDetailBinding
import com.jploran.cineexplorer.utils.Constants
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentMovie: MovieDto
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentUser = auth.currentUser!!
        _binding = FragmentMovieDetailBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        val movie: MovieDto? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("movie", MovieDto::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("movie")
        }

        movie?.let {
            currentMovie = it
            val formattedVoteAverage = String.format("%.1f", it.voteAverage)
            binding.titleDetailTV.text = it.title
            binding.detailOverviewTV.text = it.overview
            binding.detailDateTV.text = it.releaseDate
            binding.detailLanguageTV.text = it.language?.uppercase()
            binding.detailRatingTV.text = "$formattedVoteAverage/10"
            binding.detailAdultTV.text = if (it.adult.toBoolean()) "YES" else "NO"

            Glide.with(requireContext())
                .load(Constants.BASE_IMAGE_URL + it.posterPath)
                .into(binding.detailMovieImageView)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = binding.detailToolbar
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Adding menu provider for handling menu actions
        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: android.view.Menu, menuInflater: android.view.MenuInflater) {
                menuInflater.inflate(R.menu.movie_detail_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: android.view.MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_favorite -> {
                        val db = Firebase.firestore
                        val newFavorite = hashMapOf(
                            "movieId" to currentMovie.id,
                            "userId" to currentUser?.uid,
                            "title" to currentMovie.title,
                            "overview" to currentMovie.overview,
                            "posterPath" to currentMovie.posterPath,
                            "releaseDate" to currentMovie.releaseDate,
                            "voteAverage" to currentMovie.voteAverage,
                            "language" to currentMovie.language,
                            "adult" to currentMovie.adult
                        )

                        db.collection("FavoriteMovie")
                            .add(newFavorite)
                            .addOnSuccessListener {
                                Snackbar.make(binding.root, "Saved succesfully", 500).show()
                                findNavController().navigateUp()
                            }
                            .addOnFailureListener{
                                Snackbar.make(binding.root, "Error on saving movie", 500).show()
                            }

                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MovieDetailFragment()
    }
}