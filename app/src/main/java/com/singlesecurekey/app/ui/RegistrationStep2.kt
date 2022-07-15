package com.singlesecurekey.app.ui

import android.app.Application
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.airbnb.paris.extensions.style
import com.singlesecurekey.app.App
import com.singlesecurekey.app.R
import com.singlesecurekey.app.app.TOKEN_TAG
import com.singlesecurekey.app.dto.RegistrationField
import com.singlesecurekey.app.model.RegistrationModel
import com.google.android.material.snackbar.Snackbar
import java.lang.reflect.Method
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSuperclassOf
import org.json.JSONObject
import ru.tinkoff.decoro.Mask
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser
import ru.tinkoff.decoro.watchers.MaskFormatWatcher


class RegistrationStep2 : Fragment() {

    private lateinit var root: LinearLayout
    private lateinit var model: RegistrationModel
    private lateinit var registrationStory: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registration_step2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        root = view.findViewById(R.id.registration_fields)
        registrationStory = view.findViewById(R.id.registration_story)
        model = RegistrationModel(Application())
        model.requestRegistrationFields()

        model.fieldList.observe(viewLifecycleOwner) {
            rebuildUi(root, it)
        }
    }

    private fun rebuildUi(root: LinearLayout, elements: List<RegistrationField>) {
        root.removeAllViews()
        elements.forEach { item ->
            val clazz = getViewClass(item.inputtype)
            val dView = prepareView(clazz, item)
            root.addView(dView)
        }
    }

    private fun prepareView(clazz: Class<*>, item: RegistrationField): View? {

        val handleButtonClick = View.OnClickListener {
            Log.d(TAG, "start validation")
            validateAndPush(it.tag.toString())
        }

        val dView: View? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            clazz.constructors.first { it.parameters.size == 1 }.newInstance(context) as View?
        } else {
            clazz.declaredConstructors[0].newInstance(context) as View?
        }
        val methodSetHint: Method? = clazz.methods.firstOrNull {
            it.name == "setHint"
                    && it.genericParameterTypes.size == 1
                    && it.genericParameterTypes[0] == CharSequence::class.java
        }
        if (methodSetHint != null) {
            methodSetHint.invoke(dView, item.name)
        } else {
            val methodSetText: Method? = clazz.methods.firstOrNull {
                it.name == "setText"
                        && it.genericParameterTypes.size == 1
                        && it.genericParameterTypes[0] == CharSequence::class.java
            }
            methodSetText?.invoke(dView, item.name)
        }

        val methodInputType: Method? = clazz.methods.firstOrNull {
            it.name == "setTransformationMethod"
                    && it.genericParameterTypes.size == 1
        }
        methodInputType.let {
            if (item.inputtype == "password") {
                methodInputType?.invoke(dView, PasswordTransformationMethod.getInstance())
            }
        }
        if (clazz.kotlin.isSubclassOf(Button::class)) {
            val methodOnClick: Method? = clazz.methods.firstOrNull {
                it.name == "setOnClickListener"
            }
            methodOnClick?.invoke(dView, handleButtonClick)
            dView?.style(R.style.Theme_AskJowe_Button)
            dView?.setBackgroundResource(R.drawable.round_shape_button)
        }
        if (clazz.kotlin.isSubclassOf(EditText::class)){
            dView?.style(R.style.Theme_AskJowe_TextField)
            dView?.setBackgroundResource(R.drawable.round_shape_gray)
        }

        //костыль на время тестирования и согласования поля
        if(item.key == "phone"){
            val slots = PhoneNumberUnderscoreSlotsParser().parseSlots("+___ (__) ___-__-__")
            val inputMask: Mask = MaskImpl.createTerminated(slots)
            inputMask.insertFront("380930000000")
            val formatWatcher = MaskFormatWatcher(MaskImpl.createTerminated(slots))
            dView?.let {
                formatWatcher.installOn(it as TextView)
            }

        }

        val id = View.generateViewId()
        dView?.id = id
        dView?.tag = item.key
        return dView
    }

    private fun validateAndPush(tag: String) {
        val regInfo = JSONObject()
        root.children.forEach { item ->
            if (!item::class.isSuperclassOf(Button::class)) {
                val value = when (item::class) {
                    CheckBox::class -> (item as CheckBox).isChecked
                    EditText::class -> (item as EditText).text
                    TextView::class -> (item as TextView).text
                    else -> ""
                }
                model.fieldList.value?.firstOrNull {
                    it.key == item.tag
                }?.apply {
                    if (this.regexp?.isNotEmpty() == true)
                        if (this.regexp.toRegex().find(value.toString()) == null) {
                            Snackbar.make(
                                requireView(),
                                "Field ${item.tag} value is invalid!",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            return
                        }
                }
                item.tag.apply {
                    this as String
                    regInfo.put(this, value)
                }
            }
        }
        regInfo.put("lang", resources.configuration.locale)
        regInfo.put("email", App.prefs.getString("email", ""))
        regInfo.put("token", App.prefs.getString(TOKEN_TAG, ""))
        Log.i(TAG, regInfo.toString())
        model.registerMe(regInfo, tag)
        model.registrationProcessPassed.observe(viewLifecycleOwner) {
            showSuccessView(it)
            if (it) {
                parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, RegistrationStep3())
                    .addToBackStack(null)
                    .commit()
            } else {
                Snackbar.make(requireView(), getString(R.string.Auth_request_failure), Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showSuccessView(flag: Boolean) {
        val bigText = TextView(context)
        bigText.setTextColor(resources.getColor(R.color.logo_button))
        bigText.textSize = 24F
        bigText.text = when (flag) {
            true -> getString(R.string.Auth_request_success)
            false -> getString(R.string.Auth_request_failure)
        }
        registrationStory.addView(bigText)

        if (flag) {
            val littleText = TextView(context)
            littleText.setTextColor(resources.getColor(R.color.white))
            littleText.text = getString(R.string.Auth_request_success_1)
            registrationStory.addView(littleText)
        }
    }

    private fun getViewClass(viewName: String): Class<*> {
        val clazz: Class<*> = try {
            Class.forName("android.widget.$viewName")
        } catch (e: Exception) {
            try {
                Class.forName("androidx.appcompat.widget.$viewName")
            } catch (e1: Exception) {
                Class.forName("android.widget.TextView")
            }
        }
        return clazz
    }

    companion object {
        const val TAG = "REG_S2"
    }
}