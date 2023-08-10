package com.example.familyalbum


import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.familyalbum.databinding.ActivityProfileModifyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException

class ProfileModifyActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileModifyBinding
    var REQUEST_CONFIRM = 0
    private var imageUri: Uri? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageUri = uri
                try {
                    //confirm Activity로 이동
                    val intent = Intent(this, ProfileConfirmActivity::class.java)
                    intent.putExtra("imageUrl", imageUri.toString())
                    startActivityForResult(intent, REQUEST_CONFIRM)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        //firebase에서 기본 유저정보 불러옴.
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val currentUser = firebaseAuth.currentUser
        val uid = currentUser?.uid

        uid?.let { userId ->
            val userDocRef = firestore.collection("users").document(userId)

            userDocRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userInfo = documentSnapshot.data
                        val name = userInfo?.get("name") as? String
                        val email = userInfo?.get("email") as? String

                        val profileImageUrl = userInfo?.get("profileImageUrl") as? String
                        profileImageUrl?.let {
                            // Use Glide to load and display profile image
                            Glide.with(this)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.default_profile_image) // Placeholder image while loading
                                .error(R.drawable.default_profile_image) // Error image if loading fails
                                .circleCrop()
                                .into(binding.profileImageview)
                        }

                        binding.profileNameTextview.text = name
                        binding.profileEmailTextview.text = email
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "데이터 처리 failed", exception)
                }
        }

        // "프로필 추가" 버튼 클릭 시 이미지 선택 및 프로필 업데이트 처리
        binding.btnProfileAdd.setOnClickListener {
            openGallery() // 이미지 선택
        }

        // "프로필 저장" 버튼 클릭 시 프로필 이미지 업로드 및 Firestore 업데이트 처리
        binding.modifyBtn.setOnClickListener {
            saveProfileImage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CONFIRM && resultCode == RESULT_OK) {
            Glide.with(this)
                .load(imageUri)
                .into(binding.profileImageview)
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
