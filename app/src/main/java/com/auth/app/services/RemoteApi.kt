package com.auth.app.services

import com.auth.app.dto.ApproveAuth
import com.auth.app.dto.RegistrationField
import com.auth.app.dto.RegistrationResponce
import com.auth.app.dto.notification.Notification
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

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

    @POST("api/v2/Action/ApprovedAuth")
    suspend fun sendApproveAuth(@Body choise: ApproveAuth): Response<Void>

}