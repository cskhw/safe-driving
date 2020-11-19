package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.api.Api
import com.example.myapplication.databinding.ActivitySignupBinding
import com.example.myapplication.model.Login
import com.example.myapplication.util.Env
import com.example.myapplication.util.MyApp
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class SignUpActivity : AppCompatActivity() {
    lateinit var email: String
    lateinit var password: String
    private var bitmap: Bitmap? = null
    lateinit var file: File
    lateinit var imageView: ImageView

    companion object {
        const val TAKE_PICTURE = 1
        const val CHOOSE_PICTURE = 2
        const val FILE_PATH = "safe_driving/"
        const val FILE_NAME = "profile"
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageView = binding.activitySignupImage
        binding.activitySignupButtonBack.setOnClickListener {
            finish()
        }
        binding.activitySignupButtonSignup.setOnClickListener {
            email = binding.activitySingupUsername.text.toString()
            password = binding.activitySignupPassword.text.toString()
            if (bitmap != null) {
                file = createFileFromBitmap(bitmap!!)
                val survey = RequestBody.create(MediaType.parse("image/*"), file)
                val regex = "[a-zA-Z0-9]".toRegex()
                val fileId = regex.find(email)
                println(fileId)
                val multipart =
                        MultipartBody.Part.createFormData("profilePic", "${fileId?.value}.jpg", survey)
                val requestEmail = RequestBody.create(MediaType.parse("text/plain"), email)
                val requestPassword = RequestBody.create(MediaType.parse("text/plain"), password)
                Api.auth.signup(requestEmail, requestPassword, multipart)
                        .enqueue(object : Callback<Int> {
                            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                                if (response.code() == 200) {
                                    MyApp.shortToast(applicationContext, "회원가입에 성공했습니다.")
                                    val login = Login(email, password)
                                    Env.email = email
                                    Env.password = password
                                    Api.auth.login(login).enqueue(object : Callback<String> {
                                        override fun onResponse(
                                                call: Call<String>,
                                                response: Response<String>
                                        ) {
                                            response.body()
                                                    ?.let { token -> Api.update(authToken = token) }
                                            startActivity(
                                                    Intent(
                                                            applicationContext,
                                                            MainActivity::class.java
                                                    )
                                            )
                                        }

                                        override fun onFailure(call: Call<String>, t: Throwable) {
                                            MyApp.shortToast(applicationContext, "회원가입 실패")
                                        }

                                    })
                                } else {
                                    MyApp.shortToast(
                                            applicationContext,
                                            "유효하지 않은 아이디/비밀번호 혹은 이미 존재하는 계정입니다."
                                    )
                                }
                            }

                            override fun onFailure(call: Call<Int>, t: Throwable) {
                                println("signupFail")
                                t.printStackTrace()
                                println(t.message)
                                MyApp.shortToast(applicationContext, "회원가입에 실패했습니다.")
                            }
                        })
            } else {
                MyApp.shortToast(this, "이미지를 업로드 해주세요")
            }
        }
        binding.activitySignupButtonUpload.setOnClickListener {
            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, TAKE_PICTURE)
        }

        binding.activitySignupButtonGallery.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, CHOOSE_PICTURE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("activityResult")
        when (requestCode) {
            TAKE_PICTURE -> {
                println("TAKE_PICTURE")
                println("data: ${data?.extras?.get("data")}")
                // 비트맵 설정
                try {
                    bitmap = data?.extras?.get("data") as Bitmap
                    imageView.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    println("비트맵이 설정되지 않음")
                    return
                }
                // 비트맵 파일로 변환
            }
            CHOOSE_PICTURE -> {
                val input = data?.data?.let { contentResolver.openInputStream(it) }
                val img = BitmapFactory.decodeStream(input)
                bitmap = img
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    fun createFileFromBitmap(bitmap: Bitmap): File {
        val newFile = File(applicationContext.filesDir, "uploadimage")
        val fileOutputStream = FileOutputStream(newFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        return newFile
    }
}