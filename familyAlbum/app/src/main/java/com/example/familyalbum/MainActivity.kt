package com.example.familyalbum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.familyalbum.chat.ChatFragment
import com.example.familyalbum.databinding.ActivityMainBinding
import com.example.familyalbum.home.HomeFragment
import com.example.familyalbum.profile.ProfileFragment
import com.example.familyalbum.timeTable.TimeTableFragment
import com.example.familyalbum.tip.TipFragment

class MainActivity : AppCompatActivity(){
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
}