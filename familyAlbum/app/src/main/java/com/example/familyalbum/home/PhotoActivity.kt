package com.example.familyalbum.home

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.familyalbum.R
import com.example.familyalbum.databinding.ActivityPhotoBinding
import com.example.familyalbum.databinding.ActivityPhotoConfirmBinding

class PhotoActivity : AppCompatActivity() {

    lateinit var binding: ActivityPhotoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        val imageInfo = intent.getStringExtra("imageInfo")
//        val imageUrl = uploadImageInfo?.get(1)
        // 사진 text 같이 불러와야함.

        if (!imageInfo.isNullOrEmpty()) {
            val imageUri = Uri.parse(imageInfo)
            Glide.with(this)
                .load(imageUri)
                .into(binding.photoView)
        }

        binding.btnBack.setOnClickListener{
            //cancel 누르면 뒤로 가기
            finish()
        }

        binding.btnDelete.setOnClickListener {
            //사진 삭제 logic
        }
    }
}