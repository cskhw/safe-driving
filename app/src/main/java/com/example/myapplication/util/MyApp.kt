package com.example.myapplication.util

import android.content.Context
import android.widget.Toast
import retrofit2.Response

object MyApp {
    fun <T> checkResponse(response: Response<T>) {
        println("code: ${response.code()}")
        println("body: ${response.body()}")
        println("headers: ${response.headers()}")
        println("message: ${response.message()}")
        println("error: ${response.errorBody()}")
        println("raw: ${response.raw()}")
    }

    fun shortToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun longToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }


}