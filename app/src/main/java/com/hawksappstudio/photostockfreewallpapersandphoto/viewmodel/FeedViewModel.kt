package com.hawksappstudio.photostockfreewallpapersandphoto.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hawksappstudio.photostockfreewallpapersandphoto.model.Model
import com.hawksappstudio.photostockfreewallpapersandphoto.service.PhotoApiClient
import com.hawksappstudio.photostockfreewallpapersandphoto.service.TopicsApiClient
import com.hawksappstudio.photostockfreewallpapersandphoto.utils.PER_PAGE
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class FeedViewModel:ViewModel() {

    private val topicApiClient = TopicsApiClient()
    private val disposable = CompositeDisposable()

    val topicsData = MutableLiveData<List<Model.Topic>>()
    val topicsLoading = MutableLiveData<Boolean>()
    val topicsError = MutableLiveData<Boolean>()

    private val photoApiClient = PhotoApiClient()

    val photoData = MutableLiveData<List<Model.Photo>>()
    val photoLoading = MutableLiveData<Boolean>()
    val photoError = MutableLiveData<Boolean>()

    val photoDataTopics = MutableLiveData<List<Model.Photo>>()
    val photoDataTopicsHandle = MutableLiveData<List<Model.Photo>>()
    val photoTopicLoading = MutableLiveData<Boolean>()
    val photoTopicError = MutableLiveData<Boolean>()

    val searchData = MutableLiveData<Model.Search>()
    val searchDataHandle = MutableLiveData<Model.Search>()
    val searchError = MutableLiveData<Boolean>()
    val searchLoading = MutableLiveData<Boolean>()


    fun searchPhotoFromApiHandle(query:String,page:Int){
        disposable.add(
                photoApiClient.getSearchPhoto(query, page)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<Model.Search>() {
                            override fun onSuccess(t: Model.Search?) {
                               searchDataHandle.value = t
                                searchError.value = false
                                searchLoading.value = false
                            }

                            override fun onError(e: Throwable?) {
                                e?.printStackTrace()
                                searchError.value = true
                                Log.d("searchVmErr", "onError: $e")
                            }

                        })
        )

    }


    fun searchPhotoFromApi(query:String,page:Int){
        disposable.add(
                photoApiClient.getSearchPhoto(query, page)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<Model.Search>() {
                            override fun onSuccess(t: Model.Search?) {
                                searchData.value = t
                                searchError.value = false
                                searchLoading.value = false
                            }

                            override fun onError(e: Throwable?) {
                                e?.printStackTrace()
                                searchError.value = true
                                Log.d("searchVmErr", "onError: $e")
                            }

                        })
        )

    }



    fun photoTopicsFromApiHandle(slug: String,page: Int){
        disposable.add(
            topicApiClient.getTopicsPhoto(slug, page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Model.Photo>>() {
                    override fun onSuccess(t: List<Model.Photo>?) {
                        if (t != null) {
                            photoDataTopicsHandle.value = t
                            photoTopicLoading.value = false
                            photoTopicError.value = false
                            Log.d("tgelen", "onSuccess: $t")
                        }
                    }

                    override fun onError(e: Throwable?) {
                        photoTopicError.value = true
                        e?.printStackTrace()
                        Log.d("topicPhotoListFromApi", "onError: $e")
                    }

                })
        )
    }


    fun photoTopicsFromApi(slug:String,page:Int){
        disposable.add(
                topicApiClient.getTopicsPhoto(slug, page)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<List<Model.Photo>>() {
                            override fun onSuccess(t: List<Model.Photo>?) {
                                if (t != null) {
                                    photoDataTopics.value = t
                                    photoTopicLoading.value = false
                                    photoTopicError.value = false
                                    Log.d("tgelen", "onSuccess: $t")
                                }
                            }

                            override fun onError(e: Throwable?) {
                                photoTopicError.value = true
                                e?.printStackTrace()
                                Log.d("topicPhotoListFromApi", "onError: $e")
                            }

                        })
        )

    }
    fun photoListFromApi(page:Int){
            disposable.add(
                photoApiClient.getListPhoto(PER_PAGE, page).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<List<Model.Photo>>() {
                        override fun onSuccess(t: List<Model.Photo>?) {
                            if (t != null) {
                                photoData.value = t.map { it }
                                photoLoading.value = false
                                photoError.value = false
                            }
                        }
                        override fun onError(e: Throwable?) {
                            photoError.value = true
                            e?.printStackTrace()
                            Log.d("photoListFromApi", "onError: $e")
                        }

                    })
            )
    }



    fun topicsFromApi(){
        disposable.add(
            topicApiClient.getTopics()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Model.Topic>>() {
                    override fun onSuccess(t: List<Model.Topic>?) {
                        topicsData.value = t
                        topicsError.value = false
                        topicsLoading.value = false
                    }

                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                        topicsError.value = true
                        Log.d("topicsErr", "onError: Topics From Api error $e")
                    }


                })
        )
    }

}