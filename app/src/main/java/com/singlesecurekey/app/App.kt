package com.singlesecurekey.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import com.appsflyer.AppsFlyerLib


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.default_notification_channel_id)
            val description = getString(R.string.chanel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val CHANNEL_ID = getString(R.string.default_notification_channel_id)
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
        prefs = applicationContext.getSharedPreferences("app_data", MODE_PRIVATE)

        AppsFlyerLib.getInstance().init("QBEzfnFPwkkN4Dq6HKTVqS", null, this)
        AppsFlyerLib.getInstance().start(this)
        AppsFlyerLib.getInstance().setDebugLog(true)
    }

    companion object {
        lateinit var prefs: SharedPreferences
    }
}