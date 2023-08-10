package com.example.familyalbum.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.familyalbum.R

class GroupListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_list)

        val fragment = GroupListFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
