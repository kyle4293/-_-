package com.example.familyalbum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.familyalbum.chat.ChatFragment
import com.example.familyalbum.databinding.ActivityMainBinding
import com.example.familyalbum.home.HomeFragment
import com.example.familyalbum.profile.ProfileFragment
import com.example.familyalbum.timeTable.TimeTableFragment

class MainActivity : AppCompatActivity(){
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startTime = intent.getStringExtra("startTime")
        val endTime = intent.getStringExtra("endTime")
        val taskPlace = intent.getStringExtra("taskPlace")
        val taskName = intent.getStringExtra("taskName")
        val week = intent.getStringExtra("week")

        // 모든 정보가 null이 아닌지 확인
        if (startTime != null && endTime != null && taskPlace != null && taskName != null && week != null) {
            val fragment = TimeTableFragment().apply {
                arguments = Bundle().apply {
                    putString("startTime", startTime)
                    putString("endTime", endTime)
                    putString("taskPlace", taskPlace)
                    putString("taskName", taskName)
                    putString("week", week)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit()
        } else {
            // 오류 메시지 표시 등 필요한 처리 수행
            Toast.makeText(this, "모든 정보를 제공해야 합니다.", Toast.LENGTH_SHORT).show()
        }

        supportFragmentManager.beginTransaction().replace(R.id.main_content, HomeFragment())
            .commitAllowingStateLoss()

        binding.bottomNavigation.run {
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.menu_tip -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_content, TipFragment())
                            .commitAllowingStateLoss()
                    }
                    R.id.menu_chat -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_content, ChatFragment())
                            .commitAllowingStateLoss()
                    }
                    R.id.menu_home -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_content, HomeFragment())
                            .commitAllowingStateLoss()
                    }
                    R.id.menu_table -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_content, TimeTableFragment())
                            .commitAllowingStateLoss()
                    }
                    R.id.menu_profile -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_content, ProfileFragment())
                            .commitAllowingStateLoss()
                    }
                }
                true
            }
            selectedItemId = R.id.menu_home
        }
    }

    fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content, fragment)
            .addToBackStack(null) // 이 부분을 추가하여 백 스택에 추가합니다.
            .commitAllowingStateLoss()
    }
}