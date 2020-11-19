package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.myapplication.api.Api
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.model.Login
import com.example.myapplication.util.Env
import com.example.myapplication.util.MyApp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var email: String
    lateinit var password: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermission()
        binding.activityLoginButtonSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.activityLoginButtonLogin.setOnClickListener {
            email = binding.activityLoginUsername.text.toString()
            password = binding.activityLoginPassword.text.toString()
            Env.email = email
            Env.password = email
            val login = Login(email, password)
            Api.auth.login(login).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    println("response.code: ${response.code()}")
                    if (response.code() == 200) {
                        response.body()?.let { it1 -> Api.update(authToken = it1) }
                        println("authToken = ${Api.authToken}")
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        println("body: ${response.body()}")
                        MyApp.shortToast(applicationContext, "로그인.")
                    } else {
                        MyApp.shortToast(applicationContext, "로그인에 실패했습니다.")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    t.printStackTrace()
                    println("loginFail")
                    MyApp.shortToast(applicationContext, "로그인 실패")
                }
            })
        }
        binding.activityLoginButtonTest.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            ) {
                println("권한 설정 되있음")
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1
                )
            }
        }
    }
}
