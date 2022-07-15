package com.singlesecurekey.app.services

import android.app.Application
import android.app.IntentService
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.singlesecurekey.app.app.ACCEPT_ACTION
import com.singlesecurekey.app.app.DEMISS_ACTION
import com.singlesecurekey.app.app.NOTIFICATION_ID
import com.singlesecurekey.app.model.RegistrationModel


class QuestionService : IntentService("QuestionService") {

    lateinit var model: RegistrationModel

    override fun onCreate() {
        super.onCreate()
        model = RegistrationModel(Application())
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent(): " + intent);

        if (intent != null) {
            val action = intent.getAction();
            if (DEMISS_ACTION.equals(action)) {
                handleActionDismiss();
            } else if (ACCEPT_ACTION.equals(action)) {
                handleActionAccept();
            }
        }
    }

    private fun handleActionDismiss() {
        Log.d(TAG, "handleActionDismiss()")

        model.sendChoice(false)
        val notificationManagerCompat = NotificationManagerCompat.from(
            applicationContext
        )
        notificationManagerCompat.cancel(NOTIFICATION_ID)
    }

    private fun handleActionAccept() {
        Log.i(TAG, "accept action")

        model.sendChoice(true)
        val notificationManagerCompat = NotificationManagerCompat.from(
            applicationContext
        )
        notificationManagerCompat.cancel(NOTIFICATION_ID)
    }

    companion object {
        const val TAG = "QS_IS"

    }
}