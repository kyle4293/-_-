package com.example.familyalbum.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.familyalbum.R
import com.example.familyalbum.databinding.ActivityCreateGroupBinding
import com.example.familyalbum.databinding.ActivityMainBinding
import com.example.familyalbum.databinding.ActivitySearchGroupBinding

class SearchGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
    }

    private fun initLayout() {

    }
}