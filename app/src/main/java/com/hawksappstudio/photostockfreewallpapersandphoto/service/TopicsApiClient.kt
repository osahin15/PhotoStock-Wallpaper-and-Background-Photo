package com.hawksappstudio.photostockfreewallpapersandphoto.service

import com.hawksappstudio.photostockfreewallpapersandphoto.model.Model
import com.hawksappstudio.photostockfreewallpapersandphoto.utils.BASE_URL
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class TopicsApiClient {

    private val topicApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TopicsApi::class.java)

    fun getTopics():Single<List<Model.Topic>>{
        return topicApi.getTopics(30)
    }

}