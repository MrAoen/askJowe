package com.auth.app.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.auth.app.App
import com.auth.app.R

import com.auth.app.app.REGISTARTION_INFO
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FB registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
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