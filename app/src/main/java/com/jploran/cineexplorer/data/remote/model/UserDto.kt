package com.jploran.cineexplorer.data.remote.model

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("userId")
    var userId:String? = null,
    @SerializedName("name")
    var name:String? = null,
    @SerializedName("lastName")
    var lastName:String? = null,
    @SerializedName("email")
    var email:String? = null,
    @SerializedName("birthday")
    var birthday:String? = null,
)
