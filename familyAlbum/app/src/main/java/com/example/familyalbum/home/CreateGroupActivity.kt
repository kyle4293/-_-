package com.example.familyalbum.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.familyalbum.R
import com.example.familyalbum.databinding.ActivityCreateGroupBinding

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateGroupBinding
    private lateinit var viewModel: CreateGroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_group)
        binding.lifecycleOwner = this

        // ViewModelProvider를 사용하여 ViewModel 인스턴스 생성
        viewModel = ViewModelProvider(this).get(CreateGroupViewModel::class.java)
        binding.viewModel = viewModel

        // 옵저버 패턴을 사용하여 그룹 생성 성공 시 액티비티 종료
        viewModel.groupCreationSuccess.observe(this, Observer { isSuccess ->
            if (isSuccess) {
                finish() // 그룹 생성 성공 시 액티비티 종료
            }
        })
    }
}
