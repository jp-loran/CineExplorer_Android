package com.jploran.cineexplorer.application

import android.app.Application
import com.jploran.cineexplorer.data.MovieRepository
import com.jploran.cineexplorer.data.remote.RetrofitHelper

class CineExplorerApp: Application() {
    private val retrofit by lazy{
        RetrofitHelper().getRetrofit()
    }
    val repository by lazy {
        MovieRepository(retrofit)
    }
}