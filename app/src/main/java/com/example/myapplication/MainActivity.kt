package com.example.myapplication

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.api.Api
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.util.Env
import com.example.myapplication.util.MyApp
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    var bitmap: Bitmap? = null
    lateinit var file: File
    lateinit var imageView: ImageView
    lateinit var img: Bitmap

    companion object {
        const val TAKE_PICTURE = 1
        const val CHOOSE_PICTURE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageView = binding.activityMainImage
        binding.activityMainButtonPicture.setOnClickListener {
            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, TAKE_PICTURE)
        }

        binding.activityMainButtonUpload.setOnClickListener {
            if (bitmap != null) {
                println("token = ${Api.authToken}")
                file = convertBitmapToFile(bitmap!!)
                val survey = RequestBody.create(MediaType.parse("image/*"), file)
                val regex = "[a-zA-Z0-9]".toRegex()
                val imageId = regex.find(Env.email)
                val multipart = MultipartBody.Part.createFormData("picture", "${imageId?.value}.jpg", survey)
                Api.auth.faceCheck(multipart).enqueue(object : retrofit2.Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        println("response.code: ${response.code()}")
                        println("response.body: ${response.body()}")
                        if (response.body() == true) println("인식 성공")
                        else println("인식 실패")
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        println("네트워크 에러")
                        MyApp.shortToast(applicationContext, "네트워크를 확인해주세요.")
                    }
                })
            } else {
                MyApp.shortToast(this, "사진을 업로드 해주세요.")
            }
        }

        binding.activityMainButtonGallery.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, CHOOSE_PICTURE)
        }
    }

    fun convertBitmapToFile(bitmap: Bitmap): File {
        val newFile = File(applicationContext.filesDir, "picture")
        val out = FileOutputStream(newFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        return newFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            TAKE_PICTURE -> {
                if (resultCode == RESULT_OK && data?.hasExtra("data")!!) {
                    bitmap = data.extras?.get("data") as Bitmap
                    imageView.setImageBitmap(bitmap)
                }
            }
            CHOOSE_PICTURE -> {
                try {
                    val input = data?.data?.let { contentResolver.openInputStream(it) }
                    val img: Bitmap = BitmapFactory.decodeStream(input)
                    input?.close()
                    bitmap = img
                    imageView.setImageBitmap(img)
                } catch (e: Exception) {
                    println("이미지 가져오기 실패")
                }
            }
        }
    }
}