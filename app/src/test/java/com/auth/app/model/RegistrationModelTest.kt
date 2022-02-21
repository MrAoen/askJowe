package com.auth.app.model

import com.auth.app.dto.notification.Notification
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import junit.framework.Assert.assertEquals
import org.junit.Test

internal class RegistrationModelTest {

    @Test
    fun getAddsDataFromService() {

        val incomeData = """
            {
              "Title": "Title(string)",
              "MsgText" :"MsgText(string)",
              "TypeParams": "AuthRequest",
              "BodyParams": {
              "Id":"e0e7b016-de0d-4ad4-9b78-8583825e1b8e",
              "CustomerId ":"23286070-f1cc-451d-b666-946f70647ce1",
              "Merch": "Merch(string)"
            }
            }
        """.trimIndent()
        val mapper = jacksonObjectMapper()
            .registerKotlinModule()
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
        val notification = mapper.readValue(incomeData, Notification::class.java)
        val ads = notification.convert()
        assertEquals("\"Merch(string)\"",ads.body)
        assertEquals("Title(string)",ads.title)
    }
}