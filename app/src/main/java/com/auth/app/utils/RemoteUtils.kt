package com.auth.app.utils

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RemoteUtils {
    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpClient.Builder().build())
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun getHttpClient(baseUrl: String): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpClient.Builder().build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}