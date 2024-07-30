package com.jploran.cineexplorer.data.remote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieDto(
    @SerializedName("id")
    var id:Int?=null,

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

    @SerializedName("original_language")
    var language:String?=null,

    @SerializedName("release_date")
    var releaseDate:String?=null,
): Parcelable
