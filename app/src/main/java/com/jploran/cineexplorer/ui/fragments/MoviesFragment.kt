package com.jploran.cineexplorer.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jploran.cineexplorer.R
import com.jploran.cineexplorer.application.CineExplorerApp
import com.jploran.cineexplorer.data.MovieRepository
import com.jploran.cineexplorer.data.remote.model.MovieResponseDto
import com.jploran.cineexplorer.databinding.FragmentMoviesBinding
import com.jploran.cineexplorer.ui.adapters.MovieAdapter
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoviesFragment : Fragment() {
    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: MovieRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoviesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = (requireActivity().application as CineExplorerApp).repository

        lifecycleScope.launch {
            val call: Call<MovieResponseDto> = repository.getMovies("trending/movie/day")
            call.enqueue(object : Callback<MovieResponseDto> {
                override fun onResponse(p0: Call<MovieResponseDto>, p1: Response<MovieResponseDto>) {
                    binding.pbLoading.visibility = View.GONE
                    if (p1.isSuccessful) {
                        p1.body()?.let { movieResponse ->
                            val movies = movieResponse.results
                            Log.d("MoviesFragment", "Movies: $movies")
                            binding.rvMovies.apply {
                                layoutManager = LinearLayoutManager(requireContext())
                                adapter = MovieAdapter(movies) { movie ->
                                    val bundle = Bundle().apply {
                                        putParcelable("movie", movie)
                                    }
                                    findNavController().navigate(R.id.action_moviesFragment_to_movieDetailFragment, bundle)

                                    Log.d("MoviesFragment", "Selected movie: $movie")
                                }
                            }
                        }
                    } else {
                        Log.e("MoviesFragment", "Response not successful: ${p1.errorBody()}")
                    }
                }

                override fun onFailure(p0: Call<MovieResponseDto>, p1: Throwable) {
                    binding.pbLoading.visibility = View.GONE
                    Log.e("MoviesFragment", "Error: ${p1.message}")
                }
            })
        }
    }

}