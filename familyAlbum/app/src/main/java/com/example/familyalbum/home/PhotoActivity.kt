package com.example.familyalbum.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.familyalbum.MainActivity
import com.example.familyalbum.R
import com.example.familyalbum.databinding.ActivityPhotoBinding
import com.example.familyalbum.databinding.ActivityPhotoConfirmBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

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
        val imageUri = Uri.parse(imageInfo)
        val groupId = intent.getStringExtra("groupId")
        val groupName = intent.getStringExtra("groupName")
        val folderId = intent.getStringExtra("folderId")


        Glide.with(this)
            .load(imageUri)
            .into(binding.photoView)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnPhotoWrite.setOnClickListener {
            // 사진설명 적기
       }

        binding.btnDelete.setOnClickListener {
            deleteImageFromFolder(groupId!!, folderId!!, groupName!!, imageUri.toString())
        }
    }

    private fun deleteImageFromFolder(groupId: String, folderId: String, groupName: String, imageUri: String) {
        val firestore = FirebaseFirestore.getInstance()
        val folderRef = firestore.collection("groups").document(groupId)
            .collection("folders").document(folderId)

        folderRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val images = document.get("images") as? List<String>
                    if (images != null) {
                        val updatedImages = images.toMutableList().apply {
                            remove(imageUri)
                        }

                        folderRef.update("images", updatedImages)
                            .addOnSuccessListener {
                                val intent = Intent(this, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra("groupId", groupId) // 그룹 정보 전달
                                intent.putExtra("groupName", groupName) // 그룹 이름 전달
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { exception ->
                                // Handle the update failure
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }

}