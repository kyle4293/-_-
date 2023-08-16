package com.example.familyalbum.home

import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
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
        val imageUrl = intent.getStringExtra("imageUrl")
        val imageUri = Uri.parse(imageUrl)
        val imageDescription = intent.getStringExtra("imageDescription")
        val groupId = intent.getStringExtra("groupId")
        val groupName = intent.getStringExtra("groupName")
        val folderId = intent.getStringExtra("folderId")


        binding.photoText.text = imageDescription

        Glide.with(this)
            .load(imageUri)
            .into(binding.photoView)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnPhotoWrite.setOnClickListener {
            // 사진설명 적기
            showDescriptionDialog(imageUri.toString(), imageDescription)
        }

        binding.btnDelete.setOnClickListener {
            deleteImageFromFolder(groupId!!, folderId!!, groupName!!, imageUri.toString())
        }
    }

    private fun showDescriptionDialog(imageUri: String, currentDescription: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("사진 설명")

        val input = EditText(this)
        input.setText(currentDescription)
        input.hint = "사진에 대한 설명을 적어주세요."
        builder.setView(input)

        builder.setPositiveButton("저장") { dialog: DialogInterface, _: Int ->
            val newDescription = input.text.toString()
            if (newDescription.isNotEmpty()) {
                val groupId = intent.getStringExtra("groupId")
                val folderId = intent.getStringExtra("folderId")
                updateImageDescription(groupId, folderId, imageUri, newDescription)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("취소") { dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun updateImageDescription(groupId: String?, folderId: String?, imageUri: String, newDescription: String) {
        val firestore = FirebaseFirestore.getInstance()

        if (groupId != null && folderId != null) {
            val folderRef = firestore.collection("groups").document(groupId)
                .collection("folders").document(folderId)

            folderRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val images = document.get("images") as? List<Map<String, String>>

                        val updatedImages = images?.map { image ->
                            if (image["url"] == imageUri) {
                                mapOf("url" to imageUri, "description" to newDescription)
                            } else {
                                image
                            }
                        }

                        folderRef.update("images", updatedImages)
                            .addOnSuccessListener {
                                binding.photoText.text = newDescription // 이미지 설명 표시
                            }
                            .addOnFailureListener {
                                // Handle the update failure
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle the error
                }
        }
    }

    private fun deleteImageFromFolder(groupId: String, folderId: String, groupName: String, imageUri: String) {
        val firestore = FirebaseFirestore.getInstance()

        if (groupId.isNotEmpty() && folderId.isNotEmpty()) {
            val folderRef = firestore.collection("groups").document(groupId)
                .collection("folders").document(folderId)

            folderRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val images = document.get("images") as? List<Map<String, String>>

                        val updatedImages = images?.filter { it["url"] != imageUri }

                        folderRef.update("images", updatedImages)
                            .addOnSuccessListener {
                                val intent = Intent(this, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra("groupId", groupId) // 그룹 정보 전달
                                intent.putExtra("groupName", groupName) // 그룹 이름 전달
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                // Handle the update failure
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle the error
                }
        }
    }


}