package com.example.myapplication.api

import com.example.myapplication.api.service.AuthService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {
    var baseUrl: String = "http://localhost"
    var authToken = ""
    var okHttpClient: OkHttpClient = OkHttpClient.Builder().addInterceptor(AddOkHttpIntercepter()).build()
    var builder = Retrofit.Builder()
    lateinit var auth: AuthService
    fun update(
            baseUrl: String,
            authToken: String
    ) {

        Api.baseUrl = baseUrl
        var retrofit = builder.baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        auth = retrofit.create(AuthService::class.java)
    }

    fun AddOkHttpIntercepter() = Interceptor { chain ->
        val request = chain.request().newBuilder().removeHeader("authorization").addHeader("authorization", authToken)
        chain.proceed(request.build())
    }
}