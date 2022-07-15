package com.singlesecurekey.app.services

import com.singlesecurekey.app.dto.RegistrationField
import com.singlesecurekey.app.dto.RegistrationResponce
import com.singlesecurekey.app.dto.notification.Notification
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface RemoteApi {

    @GET("registrationfields")
    suspend fun getRegistrationFields(): Response<List<RegistrationField>>

    @POST("api/v2/Customer")
    suspend fun postRegistration(@Body body: RequestBody): Response<RegistrationResponce>

    @Headers("Content-Type: text/html; charset=utf-8")
    @GET("rules")
    suspend fun getCurrentRules(): Response<String>

    // /api/v2/Merch -- ненадо - прилетает из фб
    @POST("adsdata")
    suspend fun getAdsData(@Body data: RequestBody): Response<List<Notification>>

    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/v2/Action/ApprovedAuth")
    suspend fun sendApproveAuth(@Header("sign") sign:String, @Header("Content-Type") type:String, @Body choice: String): Response<Void>

    @GET("/qr")
    suspend fun getQrRequest(@Header("sign") sign:String,@Header("Content-type") type:String,@Query("sessionId") sessionId:String): Response<String>
}