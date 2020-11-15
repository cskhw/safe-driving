package com.example.myapplication.api.service

import retrofit2.http.POST

interface AuthService {
    @POST()
    fun login()
}