package com.auth.app.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.auth.app.App
import com.auth.app.R
import com.auth.app.app.REGISTARTION_INFO
import com.auth.app.app.TOKEN_TAG
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Firebase.messaging.getToken().addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result
            App.prefs.edit()?.putString(TOKEN_TAG, token)?.apply()
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d(TAG, msg)
        })


        val registered = App.prefs.getString("email", null)
        when {
            registered.isNullOrEmpty() -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, RegistrationStep1())
                    .addToBackStack(null)
                    .commit()
            }
            App.prefs.getString(REGISTARTION_INFO, "").isNullOrEmpty() -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, RegistrationStep2())
                    .addToBackStack(null)
                    .commit()
            }
            else -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, RegistrationStep3())
                    .addToBackStack(null)
                    .commit()
                //startActivity(Intent(applicationContext, ActionQuestion::class.java))
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        supportFragmentManager.popBackStack()
    }

    companion object {
        private const val TAG = "MAIN"
    }
}