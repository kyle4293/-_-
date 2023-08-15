package com.example.familyalbum.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.familyalbum.R
import com.example.familyalbum.databinding.ActivityFolderCreateBinding
import com.example.familyalbum.databinding.ActivityFolderModifyBinding
import com.example.familyalbum.databinding.ActivityProfileModifyBinding

class FolderModifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFolderModifyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.editFolderName.setText("기존 폴더 이름")
        binding.editFolderDescription.setText("기존 폴더 설명")

//        binding.folderImg.setImageURI()

        binding.btnFolderModify.setOnClickListener {
            //수정 logic 실행
        }

        binding.btnFolderDelete.setOnClickListener {
            //삭제 logic 실행
        }
    }
}