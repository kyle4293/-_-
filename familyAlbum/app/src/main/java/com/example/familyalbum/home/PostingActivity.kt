package com.example.familyalbum.home

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.familyalbum.MainActivity
import com.example.familyalbum.databinding.ActivityPostingBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class PostingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostingBinding
    private var groupId: String? = null
    private var groupName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        val uploadImageInfo = intent.getSerializableExtra("imageInfo") as? ArrayList<String>
//        val groupId = uploadImageInfo?.get(0)
        val uri = uploadImageInfo?.get(1)
        groupId = intent.getStringExtra("groupId")
        groupName = intent.getStringExtra("groupName")

        val imageUri = Uri.parse(uri)
        if (!uri.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUri)
                .into(binding.photoUpload)
        }

        binding.btnUpload.setOnClickListener {
            if(!uri.isNullOrEmpty() && !groupId.isNullOrEmpty()){
                uploadPhoto(imageUri, groupId!!)
            }
        }
    }


    private fun uploadPhoto(selectedImageUri: Uri, groupId: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")

        imageRef.putFile(selectedImageUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    // 업로드된 사진 정보를 그룹 정보에 저장
                    updateGroupWithImageInfo(groupId, groupName!!, downloadUrl)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Image upload failed.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateGroupWithImageInfo(groupId: String, groupName: String, imageUrl: String) {
        val firestore = FirebaseFirestore.getInstance()
        val groupRef = firestore.collection("groups").document(groupId)

        groupRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val images = documentSnapshot.get("images") as? List<String> ?: emptyList()
                    images.toMutableList().apply {
                        add(imageUrl)
                    }.let { updatedImages ->
                        // 업로드된 사진 URL을 그룹 정보에 저장
                        groupRef.update("images", updatedImages)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Image 업로드 완료", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra("groupId", groupId) // 그룹 정보 전달
                                intent.putExtra("groupName", groupName) // 그룹 이름 전달
                                startActivity(intent)
                                // 업데이트 성공 처리
                            }
                            .addOnFailureListener { exception ->
                                // 업데이트 실패 처리
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // 문서 가져오기 실패 처리
            }
    }
}