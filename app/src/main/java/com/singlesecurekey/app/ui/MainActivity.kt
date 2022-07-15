package com.singlesecurekey.app.ui

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.singlesecurekey.app.App
import com.singlesecurekey.app.R
import com.singlesecurekey.app.app.REGISTARTION_INFO
import com.singlesecurekey.app.app.TOKEN_TAG
import com.singlesecurekey.app.app.backOfficeEntry
import com.singlesecurekey.app.services.RemoteApi
import com.singlesecurekey.app.utils.RemoteUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.singlesecurekey.app.model.RegistrationModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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

        //deep link was possible open
        val action: String? = intent?.action
        val data: Uri? = intent?.data
        //TODO - possible need to move this part to 3th step
        if(data != null){
            lifecycleScope.launch {
                    RegistrationModel(application).completeDeepLink(data)
            }
        }

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