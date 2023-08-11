package com.example.familyalbum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.familyalbum.databinding.ActivityMainBinding
import com.example.familyalbum.databinding.ActivityTipEditBinding

class TipEditActivity : AppCompatActivity() {
    lateinit var binding: ActivityTipEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val tag = intent.getStringExtra("tag")
        val user = intent.getStringExtra("user")

        binding.tiptitle.text = title
        binding.content.text = content
        binding.edittag.text = tag
        binding.user.text = user
    }
}