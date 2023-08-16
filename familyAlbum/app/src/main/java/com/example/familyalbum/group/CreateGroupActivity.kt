package com.example.familyalbum.group

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.familyalbum.MainActivity
import com.example.familyalbum.R
import com.example.familyalbum.databinding.ActivityCreateGroupBinding
import com.example.familyalbum.profile.ProfileConfirmActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateGroupBinding
    private lateinit var viewModel: CreateGroupViewModel
    private var imageUri: Uri? = null
    var REQUEST_CONFIRM = 0


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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_group)
        binding.lifecycleOwner = this

        // ViewModelProvider를 사용하여 ViewModel 인스턴스 생성
        viewModel = ViewModelProvider(this).get(CreateGroupViewModel::class.java)
        binding.viewModel = viewModel

        // 옵저버 패턴을 사용하여 그룹 생성 성공 시 액티비티 종료
        viewModel.groupCreationSuccess.observe(this, Observer { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Succeeded in creating group", Toast.LENGTH_SHORT).show()
                joinAndLoadMain(viewModel.groupJoinId.value.toString())
            } else {
                // 그룹 생성 실패 시 처리
                Toast.makeText(this, "Failed to create group", Toast.LENGTH_SHORT).show()
            }
        })

        initLayout()

    }

    private fun initLayout() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnCreateGroup.setOnClickListener {
            // 그룹 생성 버튼 클릭 시 ViewModel의 함수 호출
            viewModel.onCreateGroupButtonClick()
        }

        binding.groupImg.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CONFIRM && resultCode == RESULT_OK) {
            Glide.with(this)
                .load(imageUri)
                .into(binding.groupImg)
        }
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
                        intent.putExtra("groupId", groupId) // 그룹 정보 전달
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
}
