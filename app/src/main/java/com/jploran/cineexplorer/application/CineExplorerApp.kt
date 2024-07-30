package com.jploran.cineexplorer.application

import android.app.Application
import com.jploran.cineexplorer.R
import com.jploran.cineexplorer.data.MovieRepository
import com.jploran.cineexplorer.data.remote.RetrofitHelper

class CineExplorerApp: Application() {
    private val apiKey by lazy{
        getString(R.string.MOVIE_API_KEY)
    }

    private val retrofit by lazy{
        RetrofitHelper(apiKey).getRetrofit()
    }
    val repository by lazy {
        MovieRepository(retrofit)
    }
}