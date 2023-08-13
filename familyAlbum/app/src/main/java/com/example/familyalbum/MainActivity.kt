package com.example.familyalbum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.familyalbum.chat.ChatFragment
import com.example.familyalbum.databinding.ActivityMainBinding
import com.example.familyalbum.home.HomeFragment
import com.example.familyalbum.profile.ProfileFragment
import com.example.familyalbum.task.TaskPlusData
import com.example.familyalbum.timeTable.TimeTableFragment
import com.example.familyalbum.tip.TipFragment

class MainActivity : AppCompatActivity(){
    lateinit var binding: ActivityMainBinding

    var selectedGroupId: String? = null
    var selectedGroupName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startTime = intent.getStringExtra("startTime")
        val endTime = intent.getStringExtra("endTime")
        val taskPlace = intent.getStringExtra("taskPlace")
        val taskName = intent.getStringExtra("taskName")
        val week = intent.getStringExtra("week")

        var timetablefragment = TimeTableFragment()
        var flag = 0
        // 모든 정보가 null이 아닌지 확인
        if (startTime != null && endTime != null && taskPlace != null && taskName != null && week != null) {
            val taskData = TaskPlusData(startTime, endTime, week, taskName,taskPlace)
            timetablefragment.arguments = createTaskBundle(taskData)
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_content,timetablefragment)
                .commitAllowingStateLoss()
            flag = 1

        } else {
            // 오류 메시지 표시 등 필요한 처리 수행
            supportFragmentManager.beginTransaction().replace(R.id.main_content, HomeFragment())
                .commitAllowingStateLoss() }

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
                                .replace(R.id.main_content,timetablefragment)
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
                if(flag==0){selectedItemId = R.id.menu_home}
                if(flag==1){selectedItemId = R.id.menu_table}
            }




    }

    fun changeFragmentWithGroup(groupId: String, groupName: String) {
        selectedGroupId = groupId
        selectedGroupName = groupName

        val homeFragment = HomeFragment.newInstance(groupId, groupName)
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content, homeFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun changeFragmentToChat(groupId: String) {
        val chatFragment = ChatFragment.newInstance(groupId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content, chatFragment)
            .commitAllowingStateLoss()
    }

    fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content, fragment)
            .addToBackStack(null) // 이 부분을 추가하여 백 스택에 추가합니다.
            .commitAllowingStateLoss()
    }

    fun createTaskBundle(taskData: TaskPlusData): Bundle {
        val bundle = Bundle()
        bundle.putString("startTime", taskData.startTime)
        bundle.putString("endTime", taskData.endTime)
        bundle.putString("week", taskData.week)
        bundle.putString("taskName", taskData.taskName)
        bundle.putString("taskPlace", taskData.taskPlace)
        return bundle
    }
}