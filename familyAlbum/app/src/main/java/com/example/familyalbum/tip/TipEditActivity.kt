package com.example.familyalbum

import android.R
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.ArrayAdapter
import com.example.familyalbum.databinding.ActivityTipEditBinding
import com.google.firebase.firestore.FirebaseFirestore

class TipEditActivity : AppCompatActivity() {
    lateinit var binding: ActivityTipEditBinding
    lateinit var firestore: FirebaseFirestore
    lateinit var tipId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //요 밑 세가지가 수정 전 tip 정보
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val tag = intent.getStringExtra("tag")
        binding.inputTipTitle.text =  Editable.Factory.getInstance().newEditable(title)
        binding.inputTipContent.text = Editable.Factory.getInstance().newEditable(content)

        val tagspinner = binding.tagSpinner

        val tags = listOf("의","식","주")

        val tagAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, tags)
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tagspinner.adapter = tagAdapter

        if(tag != null) {
            val defaultSelection = tag
            val position = tags.indexOf(defaultSelection)
            if (position != -1) {
                tagspinner.setSelection(position)
            }
        }

        firestore = FirebaseFirestore.getInstance()

        val query = firestore.collection("tips")
            .whereEqualTo("title", title)
            .whereEqualTo("content", content)
            .whereEqualTo("tag", tag)

        query.addSnapshotListener { querySnapshot, _ ->
            for (document in querySnapshot!!.documents) {
                tipId = document.id
            }
        }

        binding.button2.setOnClickListener {
            //여기서 DB작업을 해주면 됩니다

            //새로운 tip 정보
            val newTipTitle = binding.inputTipTitle.text.toString()
            val newTipContent = binding.inputTipContent.text.toString()
            val newTipTag = binding.tagSpinner.selectedItem.toString()

            val updateData = mapOf(
                "title" to newTipTitle,
                "content" to newTipContent,
                "tag" to newTipTag
            )

            // 해당 문서 업데이트
            firestore.collection("tips").document(tipId)
                .update(updateData)
                .addOnSuccessListener {
                    // 수정 성공 시 처리
                    finish()
                }
                .addOnFailureListener { e ->
                    // 수정 실패 시 처리
                    Log.e(TAG, "Error updating document", e)
                }
        }
    }
}