package com.example.familyalbum.task

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.familyalbum.MainActivity
import com.example.familyalbum.databinding.ActivityTaskPlusBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class TaskPlusActivity : AppCompatActivity() {
    lateinit var binding: ActivityTaskPlusBinding
    lateinit var firestore: FirebaseFirestore
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskPlusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentGroupName = intent.getStringExtra("groupName")
        val currentGroupId = intent.getStringExtra("groupId")

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fromTask", "fromTask")
            intent.putExtra("groupId", currentGroupId)
            intent.putExtra("groupName", currentGroupName)
            startActivity(intent)
        }

        lateinit var startHour: String
        lateinit var startMin: String
        lateinit var endHour: String
        lateinit var endMin: String
        lateinit var week: String

        // 스피너에 표시할 항목 배열
        var sHours = arrayListOf("09")
        for (i in 10 .. 23){
            if(i<10){
                sHours.add("0${i}")
            }else {
                sHours.add(i.toString())
            }
        }

        // ArrayAdapter를 사용하여 스피너에 항목을 연결
        val sHourAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sHours)
        sHourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 스피너에 어댑터 설정
        binding.startHourSpinner.adapter = sHourAdapter

        // 스피너에서 항목을 선택했을 때 처리
        binding.startHourSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                startHour = selectedItem
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 항목도 선택되지 않았을 때 처리
            }
        }

        // 스피너에 표시할 항목 배열
        var eHours = arrayListOf("09")
        for (i in 10 .. 23){
            if(i<10){
                eHours.add("0${i}")
            }else {
                eHours.add(i.toString())
            }
        }

        // ArrayAdapter를 사용하여 스피너에 항목을 연결
        val eHourAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, eHours)
        eHourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 스피너에 어댑터 설정
        binding.endHourSpinner.adapter = eHourAdapter

        // 스피너에서 항목을 선택했을 때 처리
        binding.endHourSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                endHour = selectedItem
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 항목도 선택되지 않았을 때 처리
            }
        }

        // 스피너에 표시할 항목 배열
        var sMins = arrayListOf("00","05")
        for (i in 2 .. 11){
            sMins.add("${5*i}")
        }

        // ArrayAdapter를 사용하여 스피너에 항목을 연결
        val sMinAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sMins)
        sMinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 스피너에 어댑터 설정
        binding.startMinSpinner.adapter = sMinAdapter

        // 스피너에서 항목을 선택했을 때 처리
        binding.startMinSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                startMin = selectedItem
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 항목도 선택되지 않았을 때 처리
            }
        }

        // 스피너에 표시할 항목 배열
        var eMins = arrayListOf("00","05")
        for (i in 2 .. 11){
            eMins.add("${5*i}")
        }

        // ArrayAdapter를 사용하여 스피너에 항목을 연결
        val eMinAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, eMins)
        eMinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 스피너에 어댑터 설정
        binding.endMinSpinner.adapter = eMinAdapter

        // 스피너에서 항목을 선택했을 때 처리
        binding.endMinSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                endMin = selectedItem
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 항목도 선택되지 않았을 때 처리
            }
        }


        // 스피너에 표시할 항목 배열
        var weeks = arrayListOf("월","화","수","목","금","토","일")

        // ArrayAdapter를 사용하여 스피너에 항목을 연결
        val weekAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, weeks)
        weekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 스피너에 어댑터 설정
        binding.weekSpinner.adapter = weekAdapter

        // 스피너에서 항목을 선택했을 때 처리
        binding.weekSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                week = selectedItem
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 항목도 선택되지 않았을 때 처리
            }
        }

        binding.button.setOnClickListener {
            val taskName = binding.inputTaskName.text.toString()
            val taskPlace = binding.inputTaskPlace.text.toString()
            val newStartTime = "${startHour}${startMin}"
            val newEndTime = "${endHour}${endMin}"

            when(week){
                "월" -> week = "mon"
                "화" -> week = "tue"
                "수" -> week = "wed"
                "목" -> week = "thu"
                "금" -> week = "fri"
                "토" -> week = "sat"
                "일" -> week = "sun"
            }

            val userName = auth.currentUser?.displayName

            // 요기서 DB에 task 추가
            if (newStartTime.toInt() < newEndTime.toInt()) {
                // 겹치는 일정 확인
                val query = firestore.collection("tasks")
                    .whereEqualTo("userName", userName)
                    .whereEqualTo("dayOfWeek", week)

                query.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val overlappingTasks = mutableListOf<DocumentSnapshot>()

                        for (document in task.result!!) {
                            val startTime = document.getString("startTime")
                            val endTime = document.getString("endTime")

                            if (startTime != null && endTime != null && isTimeOverlapping(
                                    newStartTime,
                                    newEndTime,
                                    startTime,
                                    endTime
                                )
                            ) {
                                overlappingTasks.add(document)
                            }
                        }

                        if (overlappingTasks.isEmpty()) {
                            val newTask = Task(
                                dayOfWeek = week,
                                endTime = newEndTime,
                                place = taskPlace,
                                startTime = newStartTime,
                                title = taskName,
                                userName = userName ?: "" // 사용자 이름이 없으면 빈 문자열로 설정
                            )

                            firestore.collection("tasks")
                                .add(newTask)
                                .addOnSuccessListener {
                                    // 수정 성공 시 처리
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.putExtra("groupId", currentGroupId)
                                    intent.putExtra("groupName", currentGroupName)
                                    intent.putExtra("fromTask", "fromTask")
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    // 수정 실패 시 처리
                                    Log.e(ContentValues.TAG, "Error updating document", e)
                                }
                        } else {
                            Toast.makeText(this, "시간이 겹치는 다른 일정이 있습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "일정을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "시작 시간이 종료 시간보다 큽니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun isTimeOverlapping(start1: String, end1: String, start2: String, end2: String): Boolean {
        val startTime1 = start1.toInt()
        val endTime1 = end1.toInt()
        val startTime2 = start2.toInt()
        val endTime2 = end2.toInt()

        return startTime1 < endTime2 && endTime1 > startTime2
    }
}