package com.example.familyalbum.tip

import com.example.familyalbum.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.familyalbum.MainActivity
import com.example.familyalbum.databinding.ActivityTipPlusBinding
import com.google.firebase.firestore.FirebaseFirestore

class TipPlusActivity : AppCompatActivity() {
    lateinit var binding: ActivityTipPlusBinding
    lateinit var firestore: FirebaseFirestore
    private var currentGroupId: String? = null
    private var currentGroupName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipPlusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentGroupName = intent.getStringExtra("groupName")
        val currentGroupId = intent.getStringExtra("groupId")

        firestore = FirebaseFirestore.getInstance()

        var newTipTag = "의"

        binding.tag1Button.setBackgroundResource(R.drawable.tag1)
        binding.tag1Image.setColorFilter(Color.parseColor("#94803e"))
        binding.tag1Text.setTextColor(Color.parseColor("#94803e"))

        binding.tag1Button.setOnClickListener {

            binding.tag1Button.setBackgroundResource(R.drawable.tag1)
            binding.tag1Image.setColorFilter(Color.parseColor("#94803e"))
            binding.tag1Text.setTextColor(Color.parseColor("#94803e"))

            binding.tag2Button.setBackgroundResource(R.drawable.tag0)
            binding.tag2Image.setColorFilter(ContextCompat.getColor(this, R.color.font_darkgray))
            binding.tag2Text.setTextColor(ContextCompat.getColor(this, R.color.font_darkgray))

            binding.tag3Button.setBackgroundResource(R.drawable.tag0)
            binding.tag3Image.setColorFilter(ContextCompat.getColor(this, R.color.font_darkgray))
            binding.tag3Text.setTextColor(ContextCompat.getColor(this, R.color.font_darkgray))

            newTipTag = "의"
        }
        binding.tag2Button.setOnClickListener {
            binding.tag1Button.setBackgroundResource(R.drawable.tag0)
            binding.tag1Image.setColorFilter(ContextCompat.getColor(this, R.color.font_darkgray))
            binding.tag1Text.setTextColor(ContextCompat.getColor(this, R.color.font_darkgray))

            binding.tag2Button.setBackgroundResource(R.drawable.tag2)
            binding.tag2Image.setColorFilter(Color.parseColor("#856155"))
            binding.tag2Text.setTextColor(Color.parseColor("#856155"))

            binding.tag3Button.setBackgroundResource(R.drawable.tag0)
            binding.tag3Image.setColorFilter(ContextCompat.getColor(this, R.color.font_darkgray))
            binding.tag3Text.setTextColor(ContextCompat.getColor(this, R.color.font_darkgray))

            newTipTag = "식"
        }
        binding.tag3Button.setOnClickListener {
            binding.tag1Button.setBackgroundResource(R.drawable.tag0)
            binding.tag1Image.setColorFilter(ContextCompat.getColor(this, R.color.font_darkgray))
            binding.tag1Text.setTextColor(ContextCompat.getColor(this, R.color.font_darkgray))

            binding.tag2Button.setBackgroundResource(R.drawable.tag0)
            binding.tag2Image.setColorFilter(ContextCompat.getColor(this, R.color.font_darkgray))
            binding.tag2Text.setTextColor(ContextCompat.getColor(this, R.color.font_darkgray))

            binding.tag3Button.setBackgroundResource(R.drawable.tag3)
            binding.tag3Image.setColorFilter(Color.parseColor("#6b5f58"))
            binding.tag3Text.setTextColor(Color.parseColor("#6b5f58"))

            newTipTag = "주"
        }

        binding.button2.setOnClickListener {
            val newTipTitle = binding.inputTipTitle.text.toString()
            val newTipContent = binding.inputTipContent.text.toString()
            val newTip = Tip(newTipTitle, newTipTag, newTipContent, currentGroupId!!)

            //새로운 tip정보를 db에 추가
            firestore.collection("tips")
                .add(newTip)
                .addOnSuccessListener {
                    // 추가 성공 시 처리
                    //finish() // 예를 들어, 현재 화면을 종료하거나 다른 처리 가능
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("fromTipEdit","fromTipEdit")
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("groupId", currentGroupId) // 그룹 정보 전달
                    intent.putExtra("groupName", currentGroupName) // 그룹 이름 전달
                    if (currentGroupName != null) {
                        if (currentGroupId != null) {
                            notifyTipChanged(currentGroupId, currentGroupName)
                        }
                    }
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    // 추가 실패 시 처리
                    // 예를 들어, 에러 메시지 출력 등
                }

        }
    }

    private fun notifyTipChanged(groupId: String, groupName: String) {
        // 푸시 알림을 발송하는 로직 작성
        val notificationTitle = "새로운 팁이 추가되었습니다."
        val notificationContent = "그룹 '$groupName' 에서 새로운 팁을 확인해보세요."

        // 알림 생성을 위한 설정
        val channelId = "tip_channel_id" // 푸시 알림을 위한 채널 ID
        val notificationId = System.currentTimeMillis().toInt() // 알림 고유 ID

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("groupId", groupId) // 그룹 정보 전달
        intent.putExtra("groupName", groupName) // 그룹 이름 전달

        val pendingIntent = PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setSmallIcon(R.drawable.app_logo) // 푸시 알림 아이콘
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 오레오 버전 이후에는 채널이 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Tip Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 발송
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

}