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
import kotlin.random.Random

class MoviesFragment : Fragment() {
    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: MovieRepository
    private var currentPage = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = (requireActivity().application as CineExplorerApp).repository

        setupRecyclerView()
        setupSwipeRefresh()
        loadMovies(currentPage)
    }

    private fun setupRecyclerView() {
        binding.rvMovies.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            currentPage = Random.nextInt(1, 101) // Generates a random page number between 1 and 100
            loadMovies(currentPage)
        }
    }

    private fun loadMovies(page: Int) {
        binding.pbLoading.visibility = View.VISIBLE

        lifecycleScope.launch {
            val call: Call<MovieResponseDto> = repository.getMovies("trending/movie/day?page=$page")
            call.enqueue(object : Callback<MovieResponseDto> {
                override fun onResponse(p0: Call<MovieResponseDto>, p1: Response<MovieResponseDto>) {
                    binding.pbLoading.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false

                    if (p1.isSuccessful) {
                        p1.body()?.let { movieResponse ->
                            val movies = movieResponse.results
                            Log.d("MoviesFragment", "Movies: $movies")
                            binding.rvMovies.adapter = MovieAdapter(movies) { movie ->
                                val bundle = Bundle().apply {
                                    putParcelable("movie", movie)
                                }
                                findNavController().navigate(R.id.action_moviesFragment_to_movieDetailFragment, bundle)
                                Log.d("MoviesFragment", "Selected movie: $movie")
                            }
                        }
                    } else {
                        Log.e("MoviesFragment", "Response not successful: ${p1.errorBody()}")
                    }
                }

                override fun onFailure(p0: Call<MovieResponseDto>, p1: Throwable) {
                    binding.pbLoading.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    Log.e("MoviesFragment", "Error: ${p1.message}")
                }
            })
        }
    }
}
