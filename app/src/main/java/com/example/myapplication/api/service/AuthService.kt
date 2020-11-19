package com.example.myapplication.api.service

import com.example.myapplication.model.Login
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface AuthService {
    @POST("signup")
    @Multipart
    fun signup(
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part profilePic: MultipartBody.Part
    ): Call<Int>

    @POST("login")
    fun login(
        @Body emailAndPassword: Login
    ): Call<String>

    @GET("user/check")
    fun userCheck(): Call<String>

    @POST("user/carFaceVerified")
    @Multipart
    fun faceCheck(@Part picture: MultipartBody.Part): Call<Boolean>
}