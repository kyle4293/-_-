package com.example.familyalbum

import android.content.ContentValues
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.familyalbum.chat.ChatFragment
import com.example.familyalbum.databinding.ActivityMainBinding
import com.example.familyalbum.group.GroupListFragment
import com.example.familyalbum.home.HomeFragment
import com.example.familyalbum.profile.ProfileFragment
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

        //FCM설정, Token값 가져오기
        MyFirebaseMessagingService().getFirebaseToken()

        initDynamicLink()
        
        if (intent.hasExtra("groupId") && intent.hasExtra("groupName")) {
            selectedGroupId = intent.getStringExtra("groupId")
            selectedGroupName = intent.getStringExtra("groupName")
            // 그룹 정보와 이름을 사용하여 화면 초기화 또는 처리
            Log.e(TAG, selectedGroupId.toString())
            Log.e(TAG, selectedGroupName.toString())
        }

        val fromTask = intent.getStringExtra("fromTask")
        val fromTipEdit = intent.getStringExtra("fromTipEdit")

        var flag = 0
        // 모든 정보가 null이 아닌지 확인
        if (fromTask == "fromTask") {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_content,TimeTableFragment())
                .commitAllowingStateLoss()
            flag = 1

        } else if(fromTipEdit == "fromTipEdit"){
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_content, TipFragment())
                .commitAllowingStateLoss()
            flag = 2
        }
        else {
            // 오류 메시지 표시 등 필요한 처리 수행
            supportFragmentManager.beginTransaction().replace(R.id.main_content, HomeFragment())
                .commitAllowingStateLoss()
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

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
                                .replace(R.id.main_content,TimeTableFragment())
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
                if(flag==2){selectedItemId = R.id.menu_tip}
            }




    }

    fun changeFragmentWithGroup(groupId: String, groupName: String) {
        selectedGroupId = groupId
        selectedGroupName = groupName

        // 홈 프래그먼트에 그룹 정보 전달
        val homeFragment = HomeFragment.newInstance(selectedGroupId.toString(), selectedGroupName.toString())
        changeFragment(homeFragment)
    }

    fun changeFragmentToGroupList() {
        val fragment = GroupListFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.icon_family, fragment) // R.id.fragmentContainer는 프래그먼트를 표시할 레이아웃의 ID입니다.
            .addToBackStack(null)
            .commit()
    }

    fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content, fragment)
            .addToBackStack(null) // 이 부분을 추가하여 백 스택에 추가합니다.
            .commitAllowingStateLoss()
    }

    private fun initDynamicLink() {
        val dynamicLinkData = intent.extras
        if (dynamicLinkData != null) {
            var dataStr = "DynamicLink 수신받은 값\n"
            for (key in dynamicLinkData.keySet()) {
                dataStr += "key: $key / value: ${dynamicLinkData.getString(key)}\n"
            }
        }
    }

}

