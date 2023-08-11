package com.example.familyalbum.group

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.familyalbum.MainActivity
import com.example.familyalbum.databinding.ActivitySearchGroupBinding

class SearchGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchGroupBinding
    private lateinit var viewModel: CreateGroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(CreateGroupViewModel::class.java)

        initLayout()

        // 그룹 참여 결과 옵저빙
        viewModel.groupJoinResult.observe(this, Observer { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "그룹에 참가되었습니다.", Toast.LENGTH_SHORT).show()

                // 메인 액티비티로 돌아가고 종료
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "그룹 참가에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun initLayout() {
        binding.joinGroupBtn.setOnClickListener {
            val groupId = binding.editGroupid.text.toString()

            if (groupId.isNotEmpty()) {
                // 그룹 참여 로직 실행
                viewModel.joinGroup(groupId)
            } else {
                Toast.makeText(this, "그룹 아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
