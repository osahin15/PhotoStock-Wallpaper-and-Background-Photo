package com.hawksappstudio.photostockfreewallpapersandphoto.service

import com.hawksappstudio.photostockfreewallpapersandphoto.model.Model
import com.hawksappstudio.photostockfreewallpapersandphoto.utils.BASE_URL
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PhotoApiClient {


        private val photoApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PhotosApi::class.java)

        fun getListPhoto(perPage:Int,page:Int) : Single<List<Model.Photo>>{
                return photoApi.getPhotoList(perPage,page)

        }

        fun getPhotoDetails(id:String) : Single<Model.Photo>{
                return  photoApi.getPhotoDetails(id)
        }

        fun getSearchPhoto(query:String,page:Int) : Single<Model.Search>{
                return photoApi.getSearch(query,20,page)
        }


}