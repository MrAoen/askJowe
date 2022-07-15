package com.singlesecurekey.app.ui

import android.app.Application
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.singlesecurekey.app.App
import com.singlesecurekey.app.R
import com.singlesecurekey.app.model.RegistrationModel
import com.singlesecurekey.app.utils.PicassoImageGetter


class RegistrationStep3 : Fragment() {

    lateinit var model: RegistrationModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registration_step3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = RegistrationModel(Application())
        model.getCurrentRules()
        model.rules.observe(viewLifecycleOwner) {
            displayHtml(it)
        }

        val cleanBtn = view.findViewById<Button>(R.id.ClearButton)
        cleanBtn.setOnClickListener {
            App.prefs.edit()
                .clear()
                .apply()
        }
    }

    private fun displayHtml(html: String) {
        val htmlViewer = requireView().findViewById<TextView>(R.id.rulesText)
        val imageGetter = PicassoImageGetter(resources, htmlViewer)
        val styledText = HtmlCompat.fromHtml(
            html,
            HtmlCompat.FROM_HTML_MODE_LEGACY,
            imageGetter, null
        )
        htmlViewer.movementMethod = LinkMovementMethod.getInstance()
        htmlViewer.text = styledText
    }

    companion object {
        const val TAG = "REG_S3"
    }
}