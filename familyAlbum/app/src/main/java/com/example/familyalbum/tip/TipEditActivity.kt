package com.example.familyalbum

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.familyalbum.databinding.ActivityTipEditBinding
import com.google.firebase.firestore.FirebaseFirestore

class TipEditActivity : AppCompatActivity() {
    lateinit var binding: ActivityTipEditBinding
    lateinit var firestore: FirebaseFirestore
    lateinit var tipId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentGroupId = intent.getStringExtra("groupId")
        val currentGroupName = intent.getStringExtra("groupName")

        //요 밑 세가지가 수정 전 tip 정보
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val tag = intent.getStringExtra("tag")
        binding.inputTipTitle.text =  Editable.Factory.getInstance().newEditable(title)
        binding.inputTipContent.text = Editable.Factory.getInstance().newEditable(content)
        when(tag){
            "의" ->{binding.tag1Button.setBackgroundResource(com.example.familyalbum.R.drawable.tag1)
                binding.tag1Image.setColorFilter(Color.parseColor("#94803e"))
                binding.tag1Text.setTextColor(Color.parseColor("#94803e"))

                binding.tag2Button.setBackgroundResource(com.example.familyalbum.R.drawable.tag0)
                binding.tag2Image.setColorFilter(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))
                binding.tag2Text.setTextColor(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))

                binding.tag3Button.setBackgroundResource(com.example.familyalbum.R.drawable.tag0)
                binding.tag3Image.setColorFilter(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))
                binding.tag3Text.setTextColor(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))


            }
            "식" -> {binding.tag1Button.setBackgroundResource(com.example.familyalbum.R.drawable.tag0)
                binding.tag1Image.setColorFilter(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))
                binding.tag1Text.setTextColor(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))

                binding.tag2Button.setBackgroundResource(com.example.familyalbum.R.drawable.tag2)
                binding.tag2Image.setColorFilter(Color.parseColor("#856155"))
                binding.tag2Text.setTextColor(Color.parseColor("#856155"))

                binding.tag3Button.setBackgroundResource(com.example.familyalbum.R.drawable.tag0)
                binding.tag3Image.setColorFilter(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))
                binding.tag3Text.setTextColor(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))

            }
            "주" -> { binding.tag1Button.setBackgroundResource(com.example.familyalbum.R.drawable.tag0)
                binding.tag1Image.setColorFilter(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))
                binding.tag1Text.setTextColor(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))

                binding.tag2Button.setBackgroundResource(com.example.familyalbum.R.drawable.tag0)
                binding.tag2Image.setColorFilter(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))
                binding.tag2Text.setTextColor(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))

                binding.tag3Button.setBackgroundResource(com.example.familyalbum.R.drawable.tag3)
                binding.tag3Image.setColorFilter(Color.parseColor("#6b5f58"))
                binding.tag3Text.setTextColor(Color.parseColor("#6b5f58"))

            }
        }


        firestore = FirebaseFirestore.getInstance()

        val query = firestore.collection("tips")
            .whereEqualTo("title", title)
            .whereEqualTo("content", content)
            .whereEqualTo("tag", tag)

        query.addSnapshotListener { querySnapshot, _ ->
            for (document in querySnapshot!!.documents) {
                tipId = document.id
            }
        }

        binding.back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fromTipEdit","fromTipEdit")
            intent.putExtra("groupId", currentGroupId) // 그룹 정보 전달
            intent.putExtra("groupName", currentGroupName) // 그룹 이름 전달
            startActivity(intent)
        }

        var newTipTag = tag

        binding.tag1Button.setOnClickListener {

            binding.tag1Button.setBackgroundResource(com.example.familyalbum.R.drawable.tag1)
            binding.tag1Image.setColorFilter(Color.parseColor("#94803e"))
            binding.tag1Text.setTextColor(Color.parseColor("#94803e"))

            binding.tag2Button.setBackgroundResource(com.example.familyalbum.R.drawable.tag0)
            binding.tag2Image.setColorFilter(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))
            binding.tag2Text.setTextColor(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))

            binding.tag3Button.setBackgroundResource(com.example.familyalbum.R.drawable.tag0)
            binding.tag3Image.setColorFilter(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))
            binding.tag3Text.setTextColor(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))

            newTipTag = "의"
        }
        binding.tag2Button.setOnClickListener {
            binding.tag1Button.setBackgroundResource(com.example.familyalbum.R.drawable.tag0)
            binding.tag1Image.setColorFilter(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))
            binding.tag1Text.setTextColor(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))

            binding.tag2Button.setBackgroundResource(com.example.familyalbum.R.drawable.tag2)
            binding.tag2Image.setColorFilter(Color.parseColor("#856155"))
            binding.tag2Text.setTextColor(Color.parseColor("#856155"))

            binding.tag3Button.setBackgroundResource(com.example.familyalbum.R.drawable.tag0)
            binding.tag3Image.setColorFilter(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))
            binding.tag3Text.setTextColor(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))

            newTipTag = "식"
        }
        binding.tag3Button.setOnClickListener {
            binding.tag1Button.setBackgroundResource(com.example.familyalbum.R.drawable.tag0)
            binding.tag1Image.setColorFilter(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))
            binding.tag1Text.setTextColor(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))

            binding.tag2Button.setBackgroundResource(com.example.familyalbum.R.drawable.tag0)
            binding.tag2Image.setColorFilter(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))
            binding.tag2Text.setTextColor(ContextCompat.getColor(this, com.example.familyalbum.R.color.font_darkgray))

            binding.tag3Button.setBackgroundResource(com.example.familyalbum.R.drawable.tag3)
            binding.tag3Image.setColorFilter(Color.parseColor("#6b5f58"))
            binding.tag3Text.setTextColor(Color.parseColor("#6b5f58"))

            newTipTag = "주"
        }
        //수정
        binding.button2.setOnClickListener {
            //여기서 DB작업을 해주면 됩니다

            //새로운 tip 정보
            val newTipTitle = binding.inputTipTitle.text.toString()
            val newTipContent = binding.inputTipContent.text.toString()

            val updateData = mapOf(
                "title" to newTipTitle,
                "content" to newTipContent,
                "tag" to newTipTag
            )

            // 해당 문서 업데이트
            firestore.collection("tips").document(tipId)
                .update(updateData)
                .addOnSuccessListener {
                    // 수정 성공 시 처리
                    //finish()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("fromTipEdit", "fromTipEdit")
                    intent.putExtra("groupId", currentGroupId) // 그룹 정보 전달
                    intent.putExtra("groupName", currentGroupName) // 그룹 이름 전달
                    startActivity(intent)
                    if (currentGroupId != null && currentGroupName != null) {
                        notifyTipChanged(currentGroupId, currentGroupName)
                    }
                }
                .addOnFailureListener { e ->
                    // 수정 실패 시 처리
                    Log.e(TAG, "Error updating document", e)
                }
        }

    }

    private fun notifyTipChanged(groupId: String, groupName: String) {
        // 푸시 알림을 발송하는 로직 작성
        val notificationTitle = "팁이 수정되었습니다."
        val notificationContent = "그룹 '$groupName' 에서 수정된 팁을 확인해보세요."

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
            .setSmallIcon(R.drawable.ic_dialog_info) // 푸시 알림 아이콘
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