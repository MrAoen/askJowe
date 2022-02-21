package com.auth.app.services

import android.content.Intent
import android.util.Log
import com.auth.app.App
import com.auth.app.app.BACKOFFICE_ADS_ID
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class AskJoweFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        App.prefs.edit()?.putString("token", token)?.apply()
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        startService(Intent(this, NotificationService::class.java).apply {
            this.action = BACKOFFICE_ADS_ID
            //remoteMessage.notification.body
            this.putExtra("id", remoteMessage.data.get(BACKOFFICE_ADS_ID))
        })

    }

    companion object {
        private const val TAG = "MSG_SRV"
    }

}