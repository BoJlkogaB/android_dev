package com.example.perfectweather

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_welcome.*


class Welcome : AppCompatActivity() {

    val APP_PREFERENCES = "WeatherApp"
    val APP_PREFERENCES_CurrentCity = ""

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_welcome)
        getSupportActionBar()?.hide()

        var mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        if(mSettings.getString(APP_PREFERENCES_CurrentCity, "") != "") {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        Welcome_enter.setOnClickListener {
            val editor = mSettings.edit()
            editor.putString(APP_PREFERENCES_CurrentCity, Welcome_spinner.getSelectedItem().toString())
            //editor.putString(APP_PREFERENCES_CurrentCityPosition, Welcome_spinner.getSelectedItemPosition().toString())
            editor.commit()

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}


