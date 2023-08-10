package com.example.familyalbum

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.familyalbum.databinding.FragmentChatBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : Fragment() {
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var binding: FragmentChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        messageList = ArrayList()
        messageAdapter = MessageAdapter(messageList)
        initLayout()
    }

    private fun initLayout() {
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = messageAdapter
        sendListening()
    }

    private fun sendListening() {
        binding.sendBtn.setOnClickListener {
            sendMessage()
        }

        //enter key event
        binding.messageEdit.setOnKeyListener { v, keyCode, event ->
            if ((keyCode == KeyEvent.KEYCODE_ENTER)) {
                val keyword: String by lazy {
                    if (binding.messageEdit.text.toString().isNullOrEmpty()) {
                        return@lazy ""
                    } else {
                        return@lazy ""
                    }
                }
                sendMessage()
            }
            return@setOnKeyListener false
        }
    }

    private fun sendMessage() {
        val messageText = binding.messageEdit.text.toString().trim()
        if (messageText.isNotEmpty()) {
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            //message list에 추가.
            val message = Message(messageText, "me", currentTime)
            messageList.add(message)
            hideKeyboard()
        }
    }

    private fun hideKeyboard() {
        //keyboard 내리기
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.messageEdit.windowToken, 0)

        //message line 비우기
        binding.messageEdit.setText("")
        //adapter에 notify, 가장 최근 채팅 내역으로 자동 스크롤.
        messageAdapter.notifyItemInserted(messageList.size - 1)
        binding.chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
    }
}