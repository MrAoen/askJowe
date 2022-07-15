package com.singlesecurekey.app.ui

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.singlesecurekey.app.App
import com.singlesecurekey.app.R
import com.singlesecurekey.app.app.TOKEN_TAG
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.installations.FirebaseInstallations

class RegistrationStep1 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registartion_step1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSignIn = view.findViewById<Button>(R.id.btn_get_token)
        val email = view.findViewById<TextInputLayout>(R.id.fld_email)
        val licAgree = view.findViewById<CheckBox>(R.id.licenseAgree)

        licAgree.text = Html.fromHtml(getString(R.string.lic_agree_label))
        licAgree.isClickable = true
        licAgree.movementMethod = LinkMovementMethod.getInstance()

        btnSignIn.setOnClickListener {
            val emailStr = email.editText?.text.toString()
            if (licAgree.isChecked && android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr)
                    .matches()
            ) {
                App.prefs.edit()?.putString("email", emailStr)?.apply()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, RegistrationStep2())
                    .addToBackStack(null)
                    .commit()
            } else {
                Snackbar.make(view, getString(R.string.REG_S1_error), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateToken() {
        FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val tokenId = task.result?.token
                Log.i(TAG, "found token $tokenId")
                App.prefs.edit()?.putString(TOKEN_TAG, tokenId)?.apply()
            } else {
                Log.e(TAG, getString(R.string.stp1_usr_token_problem))
            }
        }
    }

    companion object {
        private const val TAG = "REG_S1"
    }
}