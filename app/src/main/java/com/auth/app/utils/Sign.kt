package com.auth.app.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.auth.app.App
import com.auth.app.app.REGISTARTION_INFO
import com.auth.app.dto.RegistrationResponce
import com.fasterxml.jackson.databind.json.JsonMapper
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.time.Clock
import java.time.Instant
import java.util.*
import org.apache.commons.codec.binary.Base64.encodeBase64String
import org.apache.commons.codec.digest.DigestUtils


class Sign {

    fun getTimeMark():Int{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (Instant.now().epochSecond / 60)
        } else {
            (System.currentTimeMillis() / 60000)
        }.toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getSign(body:String): String {
        val timeMrk = getTimeMark()
        val regInfoStr = App.prefs.getString(REGISTARTION_INFO,null)
        val mapper = JsonMapper()
        val regInfo = mapper.readValue(regInfoStr, RegistrationResponce::class.java)
        val concat = body+regInfo.secret+timeMrk
        return getHash(concat)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getHash(input:String): String {
        return encodeBase64String(DigestUtils.getSha512Digest().digest(input.toByteArray()))
    }

}