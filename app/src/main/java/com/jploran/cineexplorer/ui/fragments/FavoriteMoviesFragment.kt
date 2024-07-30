package com.jploran.cineexplorer.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jploran.cineexplorer.data.remote.model.FavoriteMovieDto
import com.jploran.cineexplorer.databinding.FragmentFavoriteMoviesBinding
import com.jploran.cineexplorer.ui.adapters.FavoriteMovieAdapter

class FavoriteMoviesFragment : Fragment() {
    private var _binding: FragmentFavoriteMoviesBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private var favoriteMovies = mutableListOf<FavoriteMovieDto>()
    private var db = Firebase.firestore
    private lateinit var favoriteMovieAdapter: FavoriteMovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentUser = auth.currentUser!!
        _binding = FragmentFavoriteMoviesBinding.inflate(inflater, container, false)

        favoriteMovieAdapter = FavoriteMovieAdapter(favoriteMovies) {
        }

        binding.rvFavoriteMovies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoriteMovieAdapter
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvFavoriteMovies)

        fetchFavoriteMovies()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val movieToDelete = favoriteMovies[position]
            deleteMovie(movieToDelete, position)
        }
    }

    private fun deleteMovie(movie: FavoriteMovieDto, position: Int) {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Movie")
        builder.setMessage("Are you sure you want to delete this movie from your favorites?")
        builder.setPositiveButton("Yes") { dialog, which ->
            // Proceed with deletion
            db.collection("FavoriteMovie")
                .whereEqualTo("userId", currentUser.uid)
                .whereEqualTo("movieId", movie.movieId)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        db.collection("FavoriteMovie").document(document.id).delete()
                            .addOnSuccessListener {
                                favoriteMovies.removeAt(position)
                                favoriteMovieAdapter.notifyItemRemoved(position)
                                Snackbar.make(binding.root, "Movie removed from favorites", 800).show()
                                Log.d("DB DELETE", "DocumentSnapshot successfully deleted!")
                            }
                            .addOnFailureListener { e ->
                                Log.w("DB DELETE", "Error deleting document", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("DB DELETE", "Error finding document to delete", e)
                }
        }
        builder.setNegativeButton("No") { dialog, which ->
            favoriteMovieAdapter.notifyItemChanged(position)
        }
        builder.show()
    }


    private fun fetchFavoriteMovies() {
        db.collection("FavoriteMovie")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { result ->
                favoriteMovies.clear()
                for (document in result) {
                    val movieData = document.toObject(FavoriteMovieDto::class.java)
                    favoriteMovies.add(movieData)
                }
                favoriteMovieAdapter.notifyDataSetChanged()
                binding.noFavoriteMoviesTextView.visibility = if (favoriteMovies.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener {
                Log.w("DB RESULT", "ERROR", it)
            }
    }

    companion object {
        @JvmStatic
        fun newInstance() = FavoriteMoviesFragment()
    }
}
