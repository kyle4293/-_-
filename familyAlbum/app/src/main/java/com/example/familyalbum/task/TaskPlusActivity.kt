package com.example.familyalbum.task

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.familyalbum.R
import com.example.familyalbum.databinding.ActivityTaskPlusBinding


class TaskPlusActivity : AppCompatActivity() {
    lateinit var binding: ActivityTaskPlusBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskPlusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lateinit var startHour: String
        lateinit var startMin: String
        lateinit var endHour: String
        lateinit var endMin: String

        // 스피너에 표시할 항목 배열
        var sHours = arrayListOf("00")
        for (i in 1 .. 23){
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
        var eHours = arrayListOf("00")
        for (i in 1 .. 23){
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

        binding.button.setOnClickListener {
            val taskName = binding.inputTaskName.text.toString()
            val taskPlace = binding.inputTaskPlace.text.toString()
            /*Log.i(taskName,taskName)
            Log.i(taskPlace,taskPlace)
            Log.i(startHour,startHour)
            Log.i(startMin,startMin)
            Log.i(endHour,endHour)
            Log.i(endMin,endMin)*/

            val startTime = "${startHour}${startMin}"
            val endTime = "${endHour}${endMin}"

            Log.i(startTime,startTime)
            Log.i(endTime,endTime)



        }


    }
}