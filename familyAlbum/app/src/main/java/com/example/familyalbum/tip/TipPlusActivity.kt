package com.example.familyalbum.tip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.ArrayAdapter
import com.example.familyalbum.R
import com.example.familyalbum.databinding.ActivityTaskPlusBinding
import com.example.familyalbum.databinding.ActivityTipEditBinding
import com.example.familyalbum.databinding.ActivityTipPlusBinding

class TipPlusActivity : AppCompatActivity() {
    lateinit var binding: ActivityTipPlusBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipPlusBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val tagspinner = binding.tagSpinner

        val tags = listOf("의","식","주")

        val tagAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tags)
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tagspinner.adapter = tagAdapter


        binding.button2.setOnClickListener {


            //새로운 tip 정보
            val newTipTitle = binding.inputTipTitle.text.toString()
            val newTipContent = binding.inputTipContent.text.toString()
            val newTipTag = binding.tagSpinner.selectedItem.toString()


            //새로운 tip정보를 db에 추가


        }
    }
}