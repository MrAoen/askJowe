package com.auth.app.utils

import com.auth.app.App
import com.auth.app.app.REGISTARTION_INFO
import com.auth.app.dto.RegistrationResponce
import com.fasterxml.jackson.databind.json.JsonMapper
import java.util.*

class Utils {
    fun getCustomerId(): UUID? {
        val userInfoString = App.prefs.getString(REGISTARTION_INFO, null)
        val mapper = JsonMapper()
        val userInfo = mapper.readValue(userInfoString, RegistrationResponce::class.java)
        return userInfo?.customerId

    }
}