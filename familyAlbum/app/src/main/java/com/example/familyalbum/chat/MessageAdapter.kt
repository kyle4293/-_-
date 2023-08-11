package com.example.familyalbum.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.familyalbum.databinding.MessageBinding

class MessageAdapter(val messageList: ArrayList<Message>): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: MessageBinding): RecyclerView.ViewHolder(binding.root){
        init{

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]
        holder.binding.receiveMessageText.text = message.message
        holder.binding.receiveMessageTime.text = message.sendTime
        holder.binding.receiveMessageSender.text = message.sendId
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

}