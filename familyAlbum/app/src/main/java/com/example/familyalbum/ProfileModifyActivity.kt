package com.example.familyalbum

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.example.familyalbum.databinding.ActivityProfileModifyBinding

class ProfileModifyActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileModifyBinding
    var PICK_IMAGE_FROM_ALBUM = 0
    var photoUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
    }

    private fun initLayout() {
        binding.btnProfileAdd.setOnClickListener {
            //click 시 album 열리고 사진 선택 가능하게.
            cameraAction()
        }
    }

    val permissionLauncher
            = registerForActivityResult(ActivityResultContracts.RequestPermission()){

        Log.e("tag","33333")
        if(it){
            cameraAction()
        }else{
            Toast.makeText(this,"권한승인이 거부되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

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

    fun cameraAction(){
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode == Activity.RESULT_OK){
                photoUri = data?.data
                binding.profileImageview.setImageURI(photoUri)
            }else{
                finish()
            }
        }
    }
}