package com.example.myapplication.api

import com.example.myapplication.api.service.AuthService
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {
    var baseUrl: String = "http://bomnae1971.site:8090/"
    var builder = Retrofit.Builder()
    var authToken = ""
    lateinit var auth: AuthService


    fun update(
//        baseUrl: String = "http://bomnae1971.site:8090/",
            baseUrl: String = "http://14.32.217.141:8080",
            authToken: String = ""
    ) {
        val gson = GsonBuilder().setLenient().create()
        Api.baseUrl = baseUrl
        Api.authToken = authToken
        val okHttpClient: OkHttpClient =
                OkHttpClient.Builder().addInterceptor(addOkHttpInterceptor(authToken)).build()
        val retrofit = builder.baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        auth = retrofit.create(AuthService::class.java)
    }

    private fun addOkHttpInterceptor(authToken: String) = Interceptor { chain ->
        val request = chain.request().newBuilder().removeHeader("X-Auth-token")
                .addHeader("X-Auth-token", authToken)
        chain.proceed(request.build())
    }
}