package com.jploran.cineexplorer.data

import com.jploran.cineexplorer.data.remote.MoviesApi
import com.jploran.cineexplorer.data.remote.model.MovieDto
import com.jploran.cineexplorer.data.remote.model.MovieResponseDto
import retrofit2.Call
import retrofit2.Retrofit

class MovieRepository(private val retrofit: Retrofit) {
    private val movieApi: MoviesApi = retrofit.create(MoviesApi::class.java)
    fun getMovies(url:String?): Call<MovieResponseDto> = movieApi.getMovies(url)
}