package com.hawksappstudio.photostockfreewallpapersandphoto.model

import com.google.gson.annotations.SerializedName
import retrofit2.http.Url

class Model {

    //UIGxeHlXUJKZtsaj690S05zfFsMJKO0NSWdCWg2E5tc Access-key




    data class Photo(
            @SerializedName("id")
            val id:String,
            @SerializedName("likes")
            val likes:Int,
            @SerializedName("downloads")
            val downloads:Int,
            @SerializedName("exif")
            val exif:Exif,
            @SerializedName("location")
            val location:Location,
            @SerializedName("tags")
            val tags:ArrayList<Tags>,
            @SerializedName("urls")
            val urls:Urls,
            @SerializedName("user")
            val user:User,
            @SerializedName("blur_hash")
            val blurHash : String?=null,
            @SerializedName("width")
            val width:Int,
            @SerializedName("height")
            val height:Int
    )
    data class Tags(
            @SerializedName("title")
            val title:String,
    )
    data class Location(
            @SerializedName("city")
            val city:String,
            @SerializedName("country")
            val country:String
    )
    data class Exif(
            @SerializedName("model")
            val model:String
    )

    data class User(
            @SerializedName("id")
            val id:String,
            @SerializedName("name")
            val name:String,
            @SerializedName("links")
            val links:Links
    )

    data class Links(
            @SerializedName("html")
            val html:String
    )


    data class Urls(
        @SerializedName("thumb")
        val thumb:String,
        @SerializedName("full")
        val full:String,
    )
    

    data class Topic(
            @SerializedName("id")
            val id : String,
            @SerializedName("slug")
            val slug : String,
            @SerializedName("title")
            val title: String
    )


}