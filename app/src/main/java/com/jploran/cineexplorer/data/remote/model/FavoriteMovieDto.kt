package com.jploran.cineexplorer.data.remote.model

import com.google.gson.annotations.SerializedName

data class FavoriteMovieDto(
    @SerializedName("userId")
    var userId:String?=null,

    @SerializedName("movieId")
    var movieId:Int?=null,

    @SerializedName("title")
    var title:String?=null,

    @SerializedName("poster_path")
    var posterPath:String?=null,

    @SerializedName("vote_average")
    var voteAverage:Float?=null,

    @SerializedName("overview")
    var overview:String?=null,

    @SerializedName("adult")
    var adult:String?=null,

    @SerializedName("language")
    var language:String?=null,

    @SerializedName("release_date")
    var releaseDate:String?=null,
)
