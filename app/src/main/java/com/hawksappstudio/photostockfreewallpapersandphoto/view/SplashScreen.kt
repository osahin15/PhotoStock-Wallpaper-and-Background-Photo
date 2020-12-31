package com.hawksappstudio.photostockfreewallpapersandphoto.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.WindowManager
import com.hawksappstudio.photostockfreewallpapersandphoto.R

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.statusBarColor = Color.TRANSPARENT

        val connectionManager  = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiCon : NetworkInfo? = connectionManager.activeNetworkInfo
        val isConnected = wifiCon?.isConnectedOrConnecting == true

        if (isConnected){
            val homeIntent = Intent(this, MainActivity::class.java)
            Handler().postDelayed({
                startActivity(homeIntent)
                finish()
            },3000)
        }else{
            showCustomDialog()
        }
    }
    fun showCustomDialog(){
        var dialog  = AlertDialog.Builder(this)
        dialog.setMessage("Please connect to the internet proceed further")
            .setCancelable(false)
            .setPositiveButton("Connect") { _,_ ->
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                finish()
            }.setNegativeButton("Cancel") { _, _ ->
                startActivity(Intent(this, SplashScreen::class.java))
                finish()
            }.show()
    }
}