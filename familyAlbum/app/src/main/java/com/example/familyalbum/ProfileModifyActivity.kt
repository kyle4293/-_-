package com.example.familyalbum

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.familyalbum.databinding.ActivityProfileModifyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException

class ProfileModifyActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileModifyBinding
    private val PICK_IMAGE_REQUEST = 71
    private var imageUri: Uri? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            try {
                val source = ImageDecoder.createSource(contentResolver, uri)
                val bitmap = ImageDecoder.decodeBitmap(source)
                binding.profileImageview.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnProfileAdd.setOnClickListener {
            openGallery()
            saveProfileImage()
        }
    }

    private fun openGallery() {
        imagePickerLauncher.launch("image/*")
    }

    private fun saveProfileImage() {
        val user = firebaseAuth.currentUser
        if (user != null && imageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val profileImageRef = storageRef.child("profile_images/${user.uid}.jpg")

            profileImageRef.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // 업로드 성공한 경우
                    profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                        // 다운로드 URL을 Firestore에 저장
                        val db = FirebaseFirestore.getInstance()
                        val userDocRef = db.collection("users").document(user.uid)
                        val profileImageURL = uri.toString()
                        userDocRef.update("profileImageURL", profileImageURL)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    finish()
                                } else {
                                    // 업데이트 실패 처리
                                }
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    // 업로드 실패한 경우 처리
                }
        }
    }


//    val permissionLauncher
//            = registerForActivityResult(ActivityResultContracts.RequestPermission()){
//
//        Log.e("tag","33333")
//        if(it){
//            cameraAction()
//        }else{
//            Toast.makeText(this,"권한승인이 거부되었습니다.", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    fun permissionDialog(){
//        val builder = AlertDialog.Builder(this)
//        builder.setMessage("반드시 READ_EXTERNAL_STORAGE 권한이 허용되어야합니다.")
//            .setTitle("권한체크")
//            .setPositiveButton("OK"){
//                    _,_->
//                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
//            }.setNegativeButton("Cancel"){
//                    dlg, _-> dlg.dismiss()
//            }
//        val dlg = builder.create()
//        dlg.show()
//    }
//
//    fun cameraAction(){
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        //권한체크
//        when{
//            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
//                    == PackageManager.PERMISSION_GRANTED -> {
//                //퍼미션 허용된 경우
//
//                Log.e("tag","error111111")
//
//                intent.type = "image/*"
//                startActivity(intent)
//            }
//            ActivityCompat.shouldShowRequestPermissionRationale(this,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE) ->{
//                //명시적으로 사용자가 거부한 경우
//                permissionDialog() //dialog 띄우기.
//            }
//            else ->{
//                //권한 정보가 없는 경우 -> 권한 요청해야함.
//
//                Log.e("tag","2222222")
//
//                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
//            }
//        }
//    }
}