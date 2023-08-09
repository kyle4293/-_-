package com.example.familyalbum.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startActivity
import com.example.familyalbum.MainActivity
import com.example.familyalbum.R
import com.example.familyalbum.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>


    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        binding.loginBtn.setOnClickListener {
            val email = binding.emailEdit.text.toString()
            val password = binding.passwordEdit.text.toString()
            signInWithEmail(email, password)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.signup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.e(TAG, result.resultCode.toString())
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                // Google 로그인 결과 처리

                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    Log.e(TAG, "111111111111")

                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account?.idToken)
                } catch (e: ApiException) {
                    Log.e(TAG, "Google sign-in failed", e)
                    // Handle Google sign-in failure
                }

            }
        }

        binding.btnGoogle.setOnClickListener {
            signIn()
        }
    }

    private fun signInWithEmail(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val name = user?.displayName.toString()
                    navigateToNextScreen(name)
                } else {
                    Log.e(TAG, "Email login failed", task.exception)
                    // Handle login failure
                }
            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }


    private fun firebaseAuthWithGoogle(idToken: String?) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val name = user?.displayName.toString()
                    navigateToNextScreen(name)
                } else {
                    Log.e(TAG, "Firebase authentication failed", task.exception)
                    // Handle Firebase authentication failure
                }
            }
    }

    private fun navigateToNextScreen(user: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userName", user)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_SIGN_IN = 9001
    }


}