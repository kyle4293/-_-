package com.example.familyalbum

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.familyalbum.databinding.ActivityProfileModifyBinding

class ProfileModifyActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileModifyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
    }

    private fun initLayout() {
        binding.btnProfileAdd.setOnClickListener {
            Log.e("tag","error!!!!!!")
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

    fun permissionDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("반드시 READ_EXTERNAL_STORAGE 권한이 허용되어야합니다.")
            .setTitle("권한체크")
            .setPositiveButton("OK"){
                    _,_->
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }.setNegativeButton("Cancel"){
                    dlg, _-> dlg.dismiss()
            }
        val dlg = builder.create()
        dlg.show()
    }

    fun cameraAction(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        //권한체크
        when{
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED -> {
                //퍼미션 허용된 경우

                Log.e("tag","error111111")

                intent.type = "image/*"
                startActivity(intent)
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) ->{
                //명시적으로 사용자가 거부한 경우
                permissionDialog() //dialog 띄우기.
            }
            else ->{
                //권한 정보가 없는 경우 -> 권한 요청해야함.

                Log.e("tag","2222222")

                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
}