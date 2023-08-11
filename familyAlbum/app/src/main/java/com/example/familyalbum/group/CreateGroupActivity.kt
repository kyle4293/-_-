package com.example.familyalbum.group

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.familyalbum.MainActivity
import com.example.familyalbum.R
import com.example.familyalbum.databinding.ActivityCreateGroupBinding

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateGroupBinding
    private lateinit var viewModel: CreateGroupViewModel

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
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
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
    }
}
