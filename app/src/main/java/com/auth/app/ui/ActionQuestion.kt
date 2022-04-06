package com.auth.app.ui

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.auth.app.App
import com.auth.app.R
import com.auth.app.app.BACKOFFICE_ADS_ID
import com.auth.app.app.NOTIFICATION_ID
import com.auth.app.model.RegistrationModel
import com.auth.app.utils.Utils
import java.util.*


class ActionQuestion : AppCompatActivity() {

    private lateinit var model: RegistrationModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_action_question)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.cancel(NOTIFICATION_ID)

        model = RegistrationModel(application)

        val adsId = App.prefs.getString(BACKOFFICE_ADS_ID, null)
        if (adsId != null) {
            val userId = Utils().getCustomerId()
            userId?.let {
                model.getAddsDataFromService(it, adsId)
            }
        }
        updateUI()
        model.adsData.observe(this) {
            updateUI()
        }

        val btnTrue = findViewById<Button>(R.id.btn_accept)
        btnTrue.setOnClickListener {
            sendAndClose(true)
        }

        val btnFalse = findViewById<Button>(R.id.btn_demiss)
        btnFalse.setOnClickListener {
            sendAndClose(false)
        }

    }

    private fun sendAndClose(flag: Boolean) {
        model.sendChoice(flag)
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
        this.finish()
    }

    private fun updateUI() {
        val adsTitle = findViewById<TextView>(R.id.ads_title)
        val adsBody = findViewById<TextView>(R.id.ads_body)
        adsTitle.text = model.adsData.value?.title
        adsBody.text = model.adsData.value?.body
    }

    companion object {
        const val TAG = "AQ"
    }
}