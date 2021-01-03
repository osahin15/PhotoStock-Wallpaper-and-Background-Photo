package com.hawksappstudio.photostockfreewallpapersandphoto.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hawksappstudio.photostockfreewallpapersandphoto.model.Model
import com.hawksappstudio.photostockfreewallpapersandphoto.service.PhotoApiClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class DetailsViewModel : ViewModel() {

    private val photoApiClient = PhotoApiClient()
    private val disposable = CompositeDisposable()


    val photoData = MutableLiveData<Model.Photo>()
    val photoLoading = MutableLiveData<Boolean>()
    val photoError = MutableLiveData<Boolean>()

    fun getPhotoDetails(id:String){
        disposable.add(
                photoApiClient.getPhotoDetails(id)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<Model.Photo>() {
                            override fun onSuccess(t: Model.Photo?) {
                                photoData.value = t
                                photoLoading.value = false
                                photoError.value = false
                            }

                            override fun onError(e: Throwable?) {
                                photoError.value = true
                                e?.printStackTrace()
                                Log.d("detailsVmError", "onError: $e")
                            }

                        })
        )
    }
}