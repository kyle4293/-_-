package com.example.familyalbum.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.familyalbum.databinding.ActivityPhotoConfirmBinding

class PhotoConfirmActivity : AppCompatActivity() {
    lateinit var binding: ActivityPhotoConfirmBinding
    private var groupId: String? = null
    private var groupName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        groupId = intent.getStringExtra("groupId")
        groupName = intent.getStringExtra("groupName")
        val uploadImageInfo = intent.getSerializableExtra("imageInfo") as? ArrayList<String>
        val imageUrl = uploadImageInfo?.get(1)

        if (!imageUrl.isNullOrEmpty()) {
            Log.e(TAG, imageUrl)
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
            //confirm Activity로 이동
            val intent = Intent(this, PostingActivity::class.java)
            intent.putExtra("imageInfo", uploadImageInfo)
            intent.putExtra("groupId", groupId)
            intent.putExtra("groupName", groupName)
            startActivity(intent)
        }
    }
}