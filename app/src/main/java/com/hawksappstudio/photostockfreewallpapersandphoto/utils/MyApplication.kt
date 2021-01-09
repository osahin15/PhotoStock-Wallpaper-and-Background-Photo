package com.hawksappstudio.photostockfreewallpapersandphoto.utils

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener


class MyApplication : Application() {

    private var appOpenManager: AppOpenManager? = null


    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this, object : OnInitializationCompleteListener {
            override fun onInitializationComplete(p0: InitializationStatus?) {
            }
        })

        appOpenManager =  AppOpenManager(this);
    }
}