package com.auth.app.app

import com.auth.app.dto.notification.AuthRequest
import com.auth.app.dto.notification.Notification
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.util.*
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test


class converters_test {

    private val testObject1 = """
        [
          {
            "Title": "RequestAuth",
            "MsgText": "MerchTest send RequestAuth",
            "TimePublish": 0,
            "TimeExpiration": 60,
            "Customers": "email0@email.email",
            "TypeParams": "AuthRequest",
            "BodyParams": "{\"ID\":\"58993648-403d-48b9-bb03-b54952c8757b\",\"Customer\":{\"ID\":\"3afb3f79-fd56-43fa-8323-9a4778dc8187\",\"Email\":\"email0@email.email\",\"FirstName\":\"First Name\",\"LastName\":\"Last Name\",\"Phone\":\"+38(093)000-00-00\",\"Lang\":\"ru\",\"Pass\":\"F8D5286478A2CBC546CE9AF611F80218D4970D786A69F78BFFB57A1865ABC844ECC0E68CEEA5693FD9EA89D7D558C56B47A9A408AF7A1221433AF0FDE7E6C925\",\"Secret\":\"lpW6qaL366bZ3WEVVh9QSPtHeXWEzlhW\"},\"Merch\":{\"ID\":\"f43307ef-e7e1-4b09-a896-b738b694dc65\",\"merchName\":\"MerchTest\",\"URL\":\"https://localhost:44344/callback\",\"Secret\":\"wFtB4KZlTS6FKqQVnxUm9gc07MYvnPCb\",\"IsActive\":true},\"TimeCreateed\":0,\"State\":0}",
            "AppName": "AuthAction",
            "IsPriority": true,
            "Lang": "ru",
            "ChooseSilentNoisePlatforms": {
              "IsNotificationFieldAndroid": true,
              "IsNotificationFieldBrowser": true,
              "IsNotificationFieldIos": true
            }
          }
        ]
    """.trimIndent()

    private val testObject = """
        [
          {
            "Title": "RequestAuth",
            "MsgText": "MerchTest send RequestAuth",
            "TimePublish": 0,
            "TimeExpiration": 60,
            "Customers": "email0@email.email",
            "TypeParams": "AuthRequest",
            "BodyParams": "{\"ID\":\"58993648-403d-48b9-bb03-b54952c8757b\"}",
            "AppName": "AuthAction",
            "IsPriority": true,
            "Lang": "ru",
            "ChooseSilentNoisePlatforms": {
              "IsNotificationFieldAndroid": true,
              "IsNotificationFieldBrowser": true,
              "IsNotificationFieldIos": true
            }
          }
        ]
    """.trimIndent()

    @Test
    fun test_NotificationConverter(){
        val mapper = jacksonObjectMapper()
        val array = mapper.readValue<List<Notification>>(testObject1)
        assertFalse("OMG!!! Empty array",array.isEmpty())

        val ads = array[0].convert()
        assertEquals("RequestAuth",ads.title)
        assertEquals("MerchTest",ads.body)
    }

    @Test
    fun generate_correct_AuthRequest(){
        val a = AuthRequest(UUID.randomUUID().toString(),"customer",1000000,"merchName",10)
        val b = jacksonObjectMapper().writeValueAsString(a)
        assertTrue("It's empty!",b.isNotEmpty())
    }
}