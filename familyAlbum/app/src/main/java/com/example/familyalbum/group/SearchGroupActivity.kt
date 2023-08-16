package com.example.familyalbum.group

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.familyalbum.MainActivity
import com.example.familyalbum.databinding.ActivitySearchGroupBinding
import com.example.familyalbum.home.Folder
import com.google.firebase.firestore.FirebaseFirestore

class SearchGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchGroupBinding
    private lateinit var viewModel: CreateGroupViewModel
    private lateinit var joinGroupId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(CreateGroupViewModel::class.java)

        initLayout()

        // 그룹 참여 결과 옵저빙
        viewModel.groupJoinResult.observe(this, Observer { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "그룹에 참가되었습니다.", Toast.LENGTH_SHORT).show()
                joinAndLoadMain(joinGroupId)
            } else {
                Toast.makeText(this, "그룹 참가에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun joinAndLoadMain(groupId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val groupRef = firestore.collection("groups").document(groupId)

        groupRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val groupName = documentSnapshot.getString("groupName")
                    if (groupName != null) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("groupId", joinGroupId) // 그룹 정보 전달
                        intent.putExtra("groupName", groupName) // 그룹 이름 전달
                        startActivity(intent)
                        finish()
                    } else {
                    }
                } else {
                }
            }
            .addOnFailureListener { exception ->
                // 오류 처리
            }
    }



    private fun initLayout() {
        binding.joinGroupBtn.setOnClickListener {
            val groupId = binding.editGroupid.text.toString()

            if (groupId.isNotEmpty()) {
                // 그룹 참여 로직 실행
                viewModel.joinGroup(groupId)
                joinGroupId = groupId
            } else {
                Toast.makeText(this, "그룹 아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
