package com.singlesecurekey.app.model

import android.app.Application
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.singlesecurekey.app.App
import com.singlesecurekey.app.app.BACKOFFICE_ADS_ID
import com.singlesecurekey.app.app.REGISTARTION_INFO
import com.singlesecurekey.app.app.REGISTRATION_DELAYED
import com.singlesecurekey.app.app.backOfficeEntry
import com.singlesecurekey.app.dto.Ads
import com.singlesecurekey.app.dto.ApproveAuth
import com.singlesecurekey.app.dto.RegistrationField
import com.singlesecurekey.app.dto.notification.Notification
import com.singlesecurekey.app.services.RemoteApi
import com.singlesecurekey.app.utils.RemoteUtils
import com.singlesecurekey.app.utils.Sign
import com.singlesecurekey.app.utils.Utils
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.singlesecurekey.app.app.deepLinkUrl
import java.util.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class RegistrationModel(application: Application) : AndroidViewModel(application) {

    var fieldList = MutableLiveData<List<RegistrationField>>(emptyList())
    val registrationProcessPassed = MutableLiveData<Boolean>()
    val rules = MutableLiveData("")
    val adsData = MutableLiveData<Ads>()


    fun requestRegistrationFields() {
        var tmpList :List<RegistrationField> = listOf(
            RegistrationField(
                key ="firstName"
                , placeholder = "first name"
                , regexp = "^.{3,}\$"
                , name = "First name"
                , required = true
                , type = ""
                , inputtype = "EditText"),
            RegistrationField(
                key ="lastName"
                , placeholder = "last name"
                , regexp = "^.{3,}\$"
                , name = "Last name"
                , required = false
                , type = ""
                , inputtype = "EditText"),
            //if you won't using decoro text formatter uncomment this and comment next object
//            RegistrationField(
//                key ="phoneNumber"
//                , placeholder = "+000(00)0000000"
//                , regexp = "^\\\\+[0-9]{3}\\\\s?\\\\((\\\\d+)\\\\).?\\\\d{3}.?\\\\d{2}.?\\\\d{2}"
//                , name = "Phone number"
//                , required = true
//                , type = "phone"
//                , inputtype = "EditText"),
            RegistrationField(
                key ="phone"
                , placeholder = "+000(00)0000000"
                , regexp = ".*"
                , name = "Phone number"
                , required = true
                , type = "phone"
                , inputtype = "EditText"),
//            RegistrationField(
//                key ="nickName"
//                , placeholder = "Nick name"
//                , regexp = "^.{3,}\$"
//                , name = "Nickname"
//                , required = true
//                , type = ""
//                , inputtype = "EditText"),
            RegistrationField(
                key ="pass"
                , placeholder = "password"
                , regexp = "^.{3,}\$"
                , name = "Password"
                , required = true
                , type = ""
                , inputtype = "EditText"),
            RegistrationField(
                key ="reg_btn"
                , placeholder = ""
                , regexp = ".*"
                , name = "Register"
                , required = true
                , type = ""
                , inputtype = "Button"),
            RegistrationField(
                key ="later_btn"
                , placeholder = ""
                , regexp = ".*"
                , name = "Later"
                , required = true
                , type = ""
                , inputtype = "Button")
        )
        fieldList.value = tmpList
// if server side can send you array of fields - just uncomment and implement it!
//        viewModelScope.launch {
//            val obj = RemoteUtils().getClient(backOfficeEntry)
//                .create(RemoteApi::class.java)
//                .getRegistrationFields()
//            if (obj.isSuccessful) {
//                fieldList.value = obj.body()
//            } else {
//                Log.w(
//                    TAG,
//                    "backend return ${obj.code()} with message: ${obj.errorBody().toString()}"
//                )
//            }
//        }
    }

    fun registerMe(json: JSONObject, tag: String) {
        val mapper = JsonMapper()
        if (tag.contains("later")) {
            App.prefs.edit()?.putString(REGISTRATION_DELAYED, mapper.writeValueAsString(json))
                ?.apply()
        } else {
            viewModelScope.launch {
                val obj = RemoteUtils().getClient(backOfficeEntry)
                    .create(RemoteApi::class.java)
                    .postRegistration(
                        json.toString().toRequestBody("application/json".toMediaTypeOrNull())
                    )
                if (obj.isSuccessful) {
                    val result = obj.body()
                    registrationProcessPassed.value = result != null

                    App.prefs.edit()?.putString(REGISTRATION_DELAYED, "")?.apply()
                    App.prefs.edit()
                        ?.putString(REGISTARTION_INFO, mapper.writeValueAsString(result))?.apply()
                } else {
                    Log.e(TAG, "error request ${obj.errorBody()}")
                    registrationProcessPassed.value = false
                }
            }
        }
    }

    fun getCurrentRules() {
        viewModelScope.launch {
            val obj = RemoteUtils().getHttpClient(backOfficeEntry)
                .create(RemoteApi::class.java)
                .getCurrentRules()
            if (obj.isSuccessful) {
                rules.value = obj.body()
            }
        }
    }

    fun getAddsDataFromService(customerId: UUID, incomeData: String?){
        val mapper = jacksonObjectMapper()
            .registerKotlinModule()
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
        incomeData?.let {
            val notification = mapper.readValue(incomeData,Notification::class.java)
            val adsFromNotification = notification.convert()
            if(adsFromNotification.title.isNullOrEmpty()){
                adsFromNotification.title = notification.title ?: ""
            }
            adsData.value = adsFromNotification
        }
    }

    fun getAddsDataFromService(customerId: UUID, dataId: UUID?) {

        val mapper = jacksonObjectMapper()
            .registerKotlinModule()
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)

        dataId?.apply {
            viewModelScope.launch {
                val jsonBody = mapper.writeValueAsString(
                    ApproveAuth(dataId, customerId, null)
                ).toString().toRequestBody("application/json".toMediaTypeOrNull())

                val obj = RemoteUtils().getClient(backOfficeEntry)
                    .create(RemoteApi::class.java)
                    .getAdsData(jsonBody)

                if (obj.isSuccessful) {
                    val array = obj.body()
                    array?.first()?.apply {

                        adsData.value = this.convert()

                    }
                }
            }
        }

        Log.d(TAG, "remote result: ${adsData.value.toString()}")
    }

    //replaced with ROOM maybe soon...
    @RequiresApi(Build.VERSION_CODES.O)
    fun sendChoice(choice: Boolean) {
        val id = Utils().getAuthId()
        id?.let {
            val customerId = Utils().getCustomerId()
            customerId?.apply {

                val result = ApproveAuth(id, this, choice)
                val mapper = jacksonObjectMapper()
                    .registerKotlinModule()
                    .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                    .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
                val jsonBody = mapper.writeValueAsString(result)

                viewModelScope.launch {
                    val obj = Sign().getSign(jsonBody).let { itSign ->
                        RemoteUtils().getHttpClient(backOfficeEntry)
                            .create(RemoteApi::class.java)
                            .sendApproveAuth(itSign,"application/json", jsonBody)
                    }
                    if (obj != null) {
                        if (obj.isSuccessful) {
                            App.prefs.edit().remove(BACKOFFICE_ADS_ID).apply()
                            Log.d(TAG, "Choice successed sended")
                        } else {
                            Log.d(TAG, "Some problems with sending  choise ${obj.errorBody()?.string()}")
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun completeDeepLink(data: Uri) {
        /**
         * Запрос «начало авторизации»
         * Запрос за подписью на сервер системы
         * Отправка сохраненных данных на мерчанта с подписью полученной от системы и на URL полученный от системы
         */
        var sMerchId = ""
        var sSessionId = ""
        val qParams = data.queryParameterNames
        if(qParams.contains("merchID") && qParams.contains("sessionID")){
            sMerchId = data.getQueryParameter("merchID")?:""
            sSessionId = data.getQueryParameter("sessionID")?:""
        }
        val customerId = Utils().getCustomerId()
        customerId?.apply {
            val result = ApproveAuth(UUID.fromString(sMerchId), this, true)
            val mapper = jacksonObjectMapper()
                .registerKotlinModule()
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
            val jsonBody = mapper.writeValueAsString(result)

            viewModelScope.launch {
                val objQr =  Sign().getSign(jsonBody).let { itSign ->
                    RemoteUtils().getHttpClient(deepLinkUrl)
                        .create(RemoteApi::class.java)
                        .getQrRequest(itSign,"text/plain", sSessionId)
                }
                if (objQr.isSuccessful) {
                    objQr.headers().get("sign")?.let { respSign->
                        if(Sign().chekerSign(jsonBody,respSign)){
                            //TODO response ok - do something
                        }else{
                            Log.d(TAG,"wrong response sign")
                        }
                    }
                    Log.d(TAG, "info sent successfully")
                }
            }
        }
    }

    companion object {
        const val TAG = "RG_M"
    }
}

