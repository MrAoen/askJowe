package com.singlesecurekey.app.utils

import com.singlesecurekey.app.App
import com.singlesecurekey.app.app.BACKOFFICE_ADS_ID
import com.singlesecurekey.app.app.REGISTARTION_INFO
import com.singlesecurekey.app.dto.RegistrationResponce
import com.singlesecurekey.app.dto.notification.Merch
import com.singlesecurekey.app.dto.notification.Notification
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.util.*

class Utils {
    fun getCustomerId(): UUID? {
        val userInfoString = App.prefs.getString(REGISTARTION_INFO, null)
        val mapper = JsonMapper()
        val userInfo = mapper.readValue(userInfoString, RegistrationResponce::class.java)
        return userInfo?.customerId

    }

    fun getAuthId():UUID?{
        val notifiString = App.prefs.getString(BACKOFFICE_ADS_ID, null)
        val mapper = jacksonObjectMapper()
            .registerKotlinModule()
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
            .enable(JsonParser.Feature.IGNORE_UNDEFINED)
        val userInfo = mapper.readValue(notifiString,Notification::class.java)
        userInfo.bodyParams?.let {
            var subStr = it
            if(subStr.startsWith("\"")){
                subStr = it.substring(1,it.lastIndex)
            }
            val bodyPrm = mapper.readValue( subStr.replace("\\","") ,Merch::class.java)
            bodyPrm.id.let { idString->
                return idString
            }
        }
        return null
    }
}