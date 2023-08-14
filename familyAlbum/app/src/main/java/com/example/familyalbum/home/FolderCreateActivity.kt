package com.example.familyalbum.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.familyalbum.databinding.ActivityFolderCreateBinding
import com.example.familyalbum.databinding.ActivityPostingBinding

class FolderCreateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFolderCreateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        binding.btnCreateFolder.setOnClickListener {
            finish()
        }
    }
}