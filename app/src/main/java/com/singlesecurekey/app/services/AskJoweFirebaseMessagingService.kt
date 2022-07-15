package com.singlesecurekey.app.services

import android.content.Intent
import android.util.Log
import com.singlesecurekey.app.App
import com.singlesecurekey.app.app.BACKOFFICE_ADS_ID
import com.singlesecurekey.app.app.TOKEN_TAG
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject


class AskJoweFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        App.prefs.edit()?.putString(TOKEN_TAG, token)?.apply()
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        startService(Intent(this, NotificationService::class.java).apply {
            this.action = BACKOFFICE_ADS_ID
            val jsonComplete = JSONObject();
            remoteMessage.data.onEach {
                jsonComplete.put(it.key,it.value)
            }
            this.putExtra("id", jsonComplete.toString())//remoteMessage.data.get(BACKOFFICE_ADS_ID))
        })
    }

    companion object {
        private const val TAG = "MSG_SRV"
    }

}