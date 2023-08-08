package com.example.familyalbum.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.familyalbum.MainActivity
import com.example.familyalbum.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SignUpActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.joinBtn.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val name = binding.editTextName.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextPasswordCheck.text.toString()

            signUpWithEmail(email, password, confirmPassword, name)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signUpWithEmail(email: String, password: String, confirmPassword: String, name: String) {
        // 비밀번호 확인
        if (password != confirmPassword) {
            // 비밀번호가 일치하지 않는 경우
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // Firebase를 사용하여 회원가입
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 회원가입 성공
                    val user = FirebaseAuth.getInstance().currentUser

                    // 사용자 이름 저장
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                // 사용자 이름 저장 성공
                                // 회원가입 성공 후 수행할 작업 추가
                                navigateToNextScreen()
                            } else {
                                // 사용자 이름 저장 실패
                                Toast.makeText(this, "사용자 이름 저장 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    // 회원가입 실패
                    Toast.makeText(this, "회원가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToNextScreen() {
        // 다음 화면으로 전환하는 코드를 작성합니다.
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // 현재 액티비티를 종료하여 뒤로가기 버튼을 눌렀을 때 로그인 화면으로 돌아가지 않도록 합니다.
    }

}