package com.example.familyalbum.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.familyalbum.MainActivity
import com.example.familyalbum.databinding.FragmentChatBinding
import com.example.familyalbum.group.MemberInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : Fragment() {
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<ChatItem>
    private lateinit var binding: FragmentChatBinding
    private lateinit var currentUserID: String
    private lateinit var chatRoomId: String
    private var groupId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupId = (activity as MainActivity).selectedGroupId

        chatRoomId = "group_$groupId"

        currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        loadGroupName(groupId)

        init()
    }

    private fun init() {
        messageList = ArrayList()
        messageAdapter = MessageAdapter(messageList)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = messageAdapter

        // 채팅 메시지 불러오기 및 실시간 감지
        loadAndListenForMessages(chatRoomId)

        binding.sendBtn.setOnClickListener {
            sendMessage()
        }
    }

    private fun loadAndListenForMessages(chatRoomId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("chatRooms")
            .document(chatRoomId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { querySnapshot, _ ->
                val messages = querySnapshot?.documents?.mapNotNull { document ->
                    val senderId = document.getString("senderId")
                    val senderName = document.getString("senderName")
                    val senderImg = document.getString("senderImg")
                    val message = document.getString("message")
                    val timestamp = document.getTimestamp("timestamp")?.toDate()


                    if (senderId != null && senderName != null && senderImg != null && message != null && timestamp != null) {
                        if (senderId == currentUserID) {
                            ChatItem.MyMessage(message, senderId, timestamp)
                        } else {
                            ChatItem.OtherMessage(message, senderId, senderName, timestamp, senderImg)
                        }
                    } else {
                        null
                    }
                } ?: emptyList()

                messageAdapter.updateMessageList(messages)

                // Scroll to the bottom
                binding.chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
            }
    }

    private fun sendMessage() {
        val messageText = binding.messageEdit.text.toString().trim()
        if (messageText.isNotEmpty()) {
            val db = FirebaseFirestore.getInstance()
            val userId = currentUserID
            val userDocRef = db.collection("users").document(userId)
            userDocRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val senderName = documentSnapshot.getString("name")
                        val senderImg = documentSnapshot.getString("profileImageUrl") ?: ""
                        val messageData = hashMapOf(
                            "senderId" to userId,
                            "message" to messageText,
                            "senderName" to senderName,
                            "senderImg" to senderImg,
                            "timestamp" to FieldValue.serverTimestamp()
                        )

                        db.collection("chatRooms")
                            .document(chatRoomId)
                            .collection("messages")
                            .add(messageData)
                            .addOnSuccessListener {
                                binding.messageEdit.text.clear()

                                // 새로운 메시지가 추가되었으므로 스크롤을 아래로 이동
                                binding.chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)

                                if (userId != currentUserID && senderName != null) {
                                    sendPushNotificationToGroup(userId, senderName, messageText)
                                }
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    // 실패 시 처리 로직
                }
        }
    }

    private fun loadGroupName(groupId: String?) {
        if (groupId != null) {
            val db = FirebaseFirestore.getInstance()

            db.collection("groups")
                .document(groupId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val groupName = documentSnapshot.getString("groupName")
                    binding.groupName.text = "\t"+ groupName
                }
                .addOnFailureListener { exception ->
                    // 그룹 이름을 가져오는 데 실패한 경우 처리
                }
        }
    }

    private fun sendPushNotificationToGroup(senderId: String, senderName: String, messageText: String) {
        val groupId = groupId

        // 그룹 내의 각 사용자 토큰 가져오기 및 푸시 알림 보내기
        if (groupId != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("groups")
                .document(groupId)
                .collection("members")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val memberId = document.getString("userId")
                        val targetToken = document.getString("token")
                        if (!targetToken.isNullOrEmpty() && memberId != senderId) {
                            //발신자가 아닌 사용자에게 푸시 알림 보내기
                            sendPushNotification(targetToken, senderName, messageText)
                        }
                    }
                }
        }
    }

    private fun sendPushNotification(targetToken: String, senderName: String, messageText: String) {

    }
}