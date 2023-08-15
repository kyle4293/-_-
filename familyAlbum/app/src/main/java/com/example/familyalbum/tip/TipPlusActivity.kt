package com.example.familyalbum.tip

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.familyalbum.MainActivity
import com.example.familyalbum.R
import com.example.familyalbum.databinding.ActivityTaskPlusBinding
import com.example.familyalbum.databinding.ActivityTipEditBinding
import com.example.familyalbum.databinding.ActivityTipPlusBinding
import com.google.firebase.firestore.FirebaseFirestore

class TipPlusActivity : AppCompatActivity() {
    lateinit var binding: ActivityTipPlusBinding
    lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipPlusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentgroupName = intent.getStringExtra("groupName")
        val currentGroupId = intent.getStringExtra("groupId")
        val tagspinner = binding.tagSpinner

        val tags = listOf("의","식","주")

        val tagAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tags)
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tagspinner.adapter = tagAdapter

        firestore = FirebaseFirestore.getInstance()

        binding.button2.setOnClickListener {

            //새로운 tip 정보
            val newTipTitle = binding.inputTipTitle.text.toString()
            val newTipContent = binding.inputTipContent.text.toString()
            val newTipTag = binding.tagSpinner.selectedItem.toString()

            val newTip = Tip(newTipTitle, newTipTag, newTipContent, currentGroupId!!)

            //새로운 tip정보를 db에 추가
            firestore.collection("tips")
                .add(newTip)
                .addOnSuccessListener {
                    // 추가 성공 시 처리
                    //finish() // 예를 들어, 현재 화면을 종료하거나 다른 처리 가능
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("fromTipEdit", "fromTipEdit")
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    // 추가 실패 시 처리
                    // 예를 들어, 에러 메시지 출력 등
                }

        }
    }
}