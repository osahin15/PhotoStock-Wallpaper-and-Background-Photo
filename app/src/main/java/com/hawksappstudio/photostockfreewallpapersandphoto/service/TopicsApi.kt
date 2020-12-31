package com.hawksappstudio.photostockfreewallpapersandphoto.service

import com.hawksappstudio.photostockfreewallpapersandphoto.model.Model
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface TopicsApi {
    //Base URL
    @GET("topics/?client_id=UIGxeHlXUJKZtsaj690S05zfFsMJKO0NSWdCWg2E5tc")
    fun getTopics(@Query("per_page")perPage:Int): Single<List<Model.Topic>>
}