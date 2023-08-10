package com.example.familyalbum.profile

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.familyalbum.databinding.ActivityProfileConfirmBinding

class ProfileConfirmActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileConfirmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {

        val imageUrl= intent.getStringExtra("imageUrl")

        if (!imageUrl.isNullOrEmpty()) {
            val imageUri = Uri.parse(imageUrl)
            Glide.with(this)
                .load(imageUri)
                .into(binding.image)
        }

        binding.btnCancel.setOnClickListener{
            //cancel 누르면 뒤로 가기
            finish()
        }

        binding.btnConfirm.setOnClickListener {
            setResult(RESULT_OK, intent);
            finish()
        }
    }
}