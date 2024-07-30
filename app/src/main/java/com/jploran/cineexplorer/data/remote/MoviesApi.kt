package com.jploran.cineexplorer.data.remote

import com.jploran.cineexplorer.data.remote.model.MovieDto
import com.jploran.cineexplorer.data.remote.model.MovieResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface MoviesApi {
    @GET
    fun getMovies(@Url url: String?): Call<MovieResponseDto>
}