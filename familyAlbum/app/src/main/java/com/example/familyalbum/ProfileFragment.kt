package com.example.familyalbum

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.familyalbum.databinding.FragmentChatBinding
import com.example.familyalbum.databinding.FragmentProfileBinding
import com.example.familyalbum.user.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupProfile()

//        firebaseAuth = FirebaseAuth.getInstance()

//        val user = firebaseAuth.currentUser
//
//        val name = user?.displayName
//        val email = user?.email
//        val photoUrl = user?.photoUrl

//        if (name != null)
//            binding.profileNickname.text = name.toString()
//        if (email != null)
//            binding.profileEmail.text = email.toString()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso) // 여기서 gso는 GoogleSignInOptions 객체

        binding.signOutBtn.setOnClickListener {
            signOut()
        }

        binding.btnProfileModify.setOnClickListener {
            val intent = Intent(context, ProfileModifyActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupProfile() {
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val currentUser = firebaseAuth.currentUser
        val uid = currentUser?.uid

        uid?.let { userId ->
            val userDocRef = firestore.collection("users").document(userId)

            userDocRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userInfo = documentSnapshot.data
                        val name = userInfo?.get("name") as? String
                        val email = userInfo?.get("email") as? String
                        // 다른 필요한 정보도 가져와서 처리


                        binding.profileName.text = name
                        binding.profileEmail.text = email
                    }
                }
                .addOnFailureListener { exception ->
                    // 데이터 가져오기 실패 처리
                }
        }
    }

    private fun signOut() {
        // Firebase 로그아웃
        firebaseAuth.signOut()

        // Google 로그아웃
        googleSignInClient.signOut().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 로그아웃 성공 처리
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish() // 현재 액티비티 종료
            } else {
                // 로그아웃 실패 처리
                Log.e(TAG, "Google sign-out failed", task.exception)
                // 예외 처리 등을 수행합니다.
            }
        }
    }

}