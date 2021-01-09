package com.hawksappstudio.photostockfreewallpapersandphoto.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.*


class AppOpenManager : Application.ActivityLifecycleCallbacks {
    private val LOG_TAG = "AppOpenManager"
    private val AD_UNIT_ID = "ca-app-pub-3058271853907431/4063881247"
    private var appOpenAd: AppOpenAd? = null

    private var isShowingAd = false

    private var loadTime: Long = 0
    private var currentActivity: Activity? = null
    private var loadCallback: AppOpenAdLoadCallback? = null

    private var myApplication: MyApplication? = null

    constructor(myApplication: MyApplication?) {
        this.myApplication = myApplication
        this.myApplication?.registerActivityLifecycleCallbacks(this)

    }

    /** Request an ad  */
    fun fetchAd() {
        // We will implement this below.
        if (isAdAvailable()) {
            return;
        }

        loadCallback =
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAppOpenAdLoaded(ad: AppOpenAd?) {
                        appOpenAd = ad
                        loadTime = Date().getTime()
                    }

                    override fun onAppOpenAdFailedToLoad(p0: LoadAdError?) {
                        super.onAppOpenAdFailedToLoad(p0)
                    }
                }

       val request = getAdRequest()
        AppOpenAd.load(
                myApplication, AD_UNIT_ID, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback)

    }

    fun showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable()) {
            Log.d(LOG_TAG, "Will show ad.")
            val fullScreenContentCallback: FullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    appOpenAd = null
                    isShowingAd = false
                    fetchAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {}
                override fun onAdShowedFullScreenContent() {
                    isShowingAd = true
                }
            }
            appOpenAd!!.show(currentActivity, fullScreenContentCallback)
        } else {
            Log.d(LOG_TAG, "Can not show ad.")
            fetchAd()
        }
    }

    /** Creates and returns ad request.  */
    private fun getAdRequest(): AdRequest? {
        return AdRequest.Builder().build()
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }


    /** Utility method that checks if ad exists and can be shown.  */
    fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);

    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(p0: Activity) {
        currentActivity = p0
        showAdIfAvailable()
    }

    override fun onActivityResumed(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {
        currentActivity = null
    }


}