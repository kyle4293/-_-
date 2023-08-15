package com.example.familyalbum.home

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.familyalbum.databinding.ActivityFolderCreateBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException

class FolderCreateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFolderCreateBinding
    private var selectedImageUri: Uri? = null // 저장할 이미지의 URI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.folderImg.setOnClickListener {
            // 이미지 선택 기능 실행
            imagePickerLauncher.launch("image/*")
        }

        binding.btnCreateFolder.setOnClickListener {
            val groupId = intent.getStringExtra("groupId")
            val folderName = binding.editFolderName.text.toString()

            if (!groupId.isNullOrEmpty() && folderName.isNotEmpty() && selectedImageUri != null) {
                uploadImageAndCreateFolder(groupId, selectedImageUri!!)
            } else {
                // 예외 처리: 그룹 ID나 폴더 이름, 이미지가 없는 경우에 대한 처리
            }
        }
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri // 선택한 이미지의 URI를 저장
            binding.folderImg.setImageURI(uri) // 선택한 이미지를 ImageView에 표시
        }
    }

    private fun uploadImageAndCreateFolder(groupId: String, imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("images/${imageUri.lastPathSegment}")

        val uploadTask = imagesRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }
            imagesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val imageDownloadUrl = task.result.toString()
                val folderName = binding.editFolderName.text.toString()

                createFolderWithImage(groupId, folderName, imageDownloadUrl)
            } else {
                // Handle the error
            }
        }
    }

    private fun createFolderWithImage(groupId: String, folderName: String, imageUrl: String) {
        val groupDocRef = FirebaseFirestore.getInstance().collection("groups").document(groupId)

        val folderData = hashMapOf(
            "name" to folderName,
            "images" to arrayListOf(imageUrl) // 이미지 URL 추가
        )

        groupDocRef.collection("folders")
            .add(folderData)
            .addOnSuccessListener { folderDocRef ->
                Log.d(ContentValues.TAG, "Folder with image added with ID: ${folderDocRef.id}")

                finish() // 폴더 생성 후 액티비티 종료
            }
            .addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Error adding folder with image", e)
            }
    }
}
