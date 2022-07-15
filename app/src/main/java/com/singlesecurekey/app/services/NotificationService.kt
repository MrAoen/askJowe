package com.singlesecurekey.app.services

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.LifecycleService
import com.singlesecurekey.app.App
import com.singlesecurekey.app.R
import com.singlesecurekey.app.app.ACCEPT_ACTION
import com.singlesecurekey.app.app.APP_NOTIFICATION_ID
import com.singlesecurekey.app.app.BACKOFFICE_ADS_ID
import com.singlesecurekey.app.app.DEMISS_ACTION
import com.singlesecurekey.app.dto.Ads
import com.singlesecurekey.app.model.RegistrationModel
import com.singlesecurekey.app.ui.ActionQuestion
import com.singlesecurekey.app.utils.Utils

class NotificationService : LifecycleService() {

    private lateinit var model: RegistrationModel

    override fun onCreate() {
        super.onCreate()
        model = RegistrationModel(Application())
        model.adsData.observe(this) {
            customNotification(it)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val messageId = it.extras?.getString("id")
            Utils().getCustomerId()?.apply {
                App.prefs.edit().putString(BACKOFFICE_ADS_ID,messageId).apply()
                model.getAddsDataFromService(this, messageId)
                //model.getAddsDataFromService(this, UUID.fromString(messageId))
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun customNotification(payload: Ads) {

        val channelId = getChanelId()
        val builder = NotificationCompat.Builder(this, channelId)

        val content = payload.body
        val title = payload.title

        val smallIcon = R.drawable.small_notify_icon

        val notifyIntent = Intent(this, ActionQuestion::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val notifyPendingIntent : PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(notifyIntent)
            getPendingIntent(0, PendingIntent.FLAG_MUTABLE)//FLAG_UPDATE_CURRENT)
        }

        //Accept button intent
        val acceptIntent = Intent(this, QuestionService::class.java).apply {
            action = ACCEPT_ACTION
        }
        val pendingAcceptIntent =
            PendingIntent.getService(this, 0, acceptIntent, PendingIntent.FLAG_MUTABLE)//PendingIntent.FLAG_UPDATE_CURRENT)
        val acceptAction: NotificationCompat.Action = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(this, R.drawable.accept_question_48dp),
            "YES",
            pendingAcceptIntent
        ).build()

        //Demiss button intent
        val demissIntent = Intent(this, QuestionService::class.java).apply {
            action = DEMISS_ACTION
        }
        val pendingDemissIntent =
            PendingIntent.getService(this, 0, demissIntent, PendingIntent.FLAG_MUTABLE)//PendingIntent.FLAG_UPDATE_CURRENT)
        val demissAction: NotificationCompat.Action = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(this, R.drawable.demiss_question_48dp),
            "NO",
            pendingDemissIntent
        ).build()

        builder.setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content).setBigContentTitle(title))
            .setContentIntent(notifyPendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .addAction(acceptAction)
            .addAction(demissAction)
            .setSmallIcon(smallIcon)
            .build()

        val notificationManager = NotificationManagerCompat.from(this)

        notificationManager.notify(APP_NOTIFICATION_ID, builder.build())
    }

    private fun getChanelId(): String {
        val channelId = getString(R.string.default_notification_channel_id)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                "questions about merch",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = "advertising and entertainment"
            notificationChannel.enableVibration(true)
            notificationChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(notificationChannel)
        }
        return channelId
    }
}