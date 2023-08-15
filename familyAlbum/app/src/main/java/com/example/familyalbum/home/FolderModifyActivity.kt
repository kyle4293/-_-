package com.example.familyalbum.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.familyalbum.MainActivity
import com.example.familyalbum.R
import com.example.familyalbum.databinding.ActivityFolderCreateBinding
import com.example.familyalbum.databinding.ActivityFolderModifyBinding
import com.example.familyalbum.databinding.ActivityProfileModifyBinding
import com.google.firebase.firestore.FirebaseFirestore

class FolderModifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFolderModifyBinding
    private var groupId: String? = null
    private var groupName: String? = null
    private var folderId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        groupId = intent.getStringExtra("groupId")
        groupName = intent.getStringExtra("groupName")
        folderId = intent.getStringExtra("folderId")


        binding.btnBack.setOnClickListener {
            finish()
        }

        val firestore = FirebaseFirestore.getInstance()
        val folderRef = firestore.collection("groups").document(groupId!!)
            .collection("folders").document(folderId!!)

        folderRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val folderName = document.getString("name")
                    val folderDescription = document.getString("description")

                    binding.editFolderName.setText(folderName)
                    binding.editFolderDescription.setText(folderDescription)
                }
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }


//        binding.folderImg.setImageURI()

        binding.btnFolderModify.setOnClickListener {
            val newName = binding.editFolderName.text.toString()
            val newDescription = binding.editFolderDescription.text.toString()

            // 수정 로직 실행
            modifyFolder(groupId!!, groupName!!, folderId!!, newName, newDescription)
        }

        binding.btnFolderDelete.setOnClickListener {
            // 삭제 로직 실행
            deleteFolder(groupId!!, groupName!!, folderId!!)
        }
    }

    private fun modifyFolder(groupId: String, groupName: String, folderId: String, newName: String, newDescription: String) {
        val firestore = FirebaseFirestore.getInstance()
        val folderRef = firestore.collection("groups").document(groupId)
            .collection("folders").document(folderId)

        val data = hashMapOf(
            "name" to newName,
            "description" to newDescription
            // 다른 필드도 필요하다면 추가
        )

        folderRef.update(data as Map<String, Any>)
            .addOnSuccessListener {
                // 수정 성공
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("groupId", groupId) // 그룹 정보 전달
                intent.putExtra("groupName", groupName) // 그룹 이름 전달
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { exception ->
                // 수정 실패 처리
            }
    }

    private fun deleteFolder(groupId: String, groupName: String, folderId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val folderRef = firestore.collection("groups").document(groupId)
            .collection("folders").document(folderId)

        folderRef.delete()
            .addOnSuccessListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("groupId", groupId) // 그룹 정보 전달
                intent.putExtra("groupName", groupName) // 그룹 이름 전달
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { exception ->
                // 삭제 실패 처리
            }
    }
}