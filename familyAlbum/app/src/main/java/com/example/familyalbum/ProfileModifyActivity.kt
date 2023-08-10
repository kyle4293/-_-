package com.example.familyalbum

import android.content.ContentValues.TAG
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.familyalbum.databinding.ActivityProfileModifyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException

class ProfileModifyActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileModifyBinding
    private var imageUri: Uri? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageUri = uri
                try {
                    val source = ImageDecoder.createSource(contentResolver, uri)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    binding.profileImageview.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnProfileAdd.setOnClickListener {
            openGallery()
            saveProfileImage()
        }
    }

    private fun openGallery() {
        imagePickerLauncher.launch("image/*")
    }

    private fun saveProfileImage() {

        val user = firebaseAuth.currentUser
        if (user != null && imageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val profileImageRef = storageRef.child("profile_images/${user.uid}.jpg")

            profileImageRef.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // 업로드 성공한 경우

                    profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                        // 다운로드 URL을 Firestore에 저장
                        val db = FirebaseFirestore.getInstance()
                        val userDocRef = db.collection("users").document(user.uid)
                        val profileImageURL = uri.toString()
                        userDocRef.update("profileImageUrl", profileImageURL)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    finish()
                                } else {
                                    // 업데이트 실패 처리
                                }
                            }
                    }


                }
                .addOnFailureListener { exception ->
                    // 업로드 실패한 경우 처리
                    Log.e(TAG, "프로필 업로드 failed", exception)

                }
        }
    }

}
