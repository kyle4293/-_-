package com.example.familyalbum.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.familyalbum.R
import com.example.familyalbum.databinding.MessageBinding
import com.example.familyalbum.databinding.MymessageBinding
import kotlinx.coroutines.NonDisposableHandle.parent

class MessageAdapter(val messageList: ArrayList<ChatItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    inner class ViewHolder(val binding: MessageBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_MY -> {
//                val binding = MymessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//                return ViewHolder(binding)
            MyMessageHolder.create(parent)
        }
        TYPE_OTHER -> {
            OtherMessageHolder.create(parent)
        }
        else -> {
            throw IllegalStateException("Not Found ViewHolder Type $viewType")
        }
    }

    override fun getItemViewType(position: Int) = when (messageList[position]) {
        is ChatItem.MyMessage -> {
            TYPE_MY
        }
        is ChatItem.OtherMessage -> {
            TYPE_OTHER
        }
        else -> {
            throw IllegalStateException("Not Found ViewHolder Type")
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is MyMessageHolder -> {
                holder.bind(messageList[position] as ChatItem.MyMessage)
            }
            is OtherMessageHolder -> {
                holder.bind(messageList[position] as ChatItem.OtherMessage)
            }
//        }
//        val message = messageList[position]
//        holder.binding.receiveMessageText.text = message.message
//        holder.binding.receiveMessageTime.text = message.timestamp.toString() // 수정 필요
//        holder.binding.receiveMessageSender.text = message.senderId
        }
    }

    class MyMessageHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){
        private val messageText = itemView.findViewById<TextView>(R.id.send_message_text)
        private val messageTime = itemView.findViewById<TextView>(R.id.send_message_time)

        fun bind(message: ChatItem.MyMessage){
            messageText.text = message.message
            messageTime.text = message.timestamp.toString()
        }
        companion object Factory{
            fun create(parent: ViewGroup): MyMessageHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.mymessage, parent, false)

                return MyMessageHolder(view)
            }
        }
    }

    class OtherMessageHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){
        private val messageText = itemView.findViewById<TextView>(R.id.receive_message_text)
        private val messageTime = itemView.findViewById<TextView>(R.id.receive_message_time)
        private val senderImg = itemView.findViewById<ImageView>(R.id.receive_message_img)
        private val senderName = itemView.findViewById<TextView>(R.id.receive_message_sender)



        fun bind(message: ChatItem.OtherMessage){
            messageText.text = message.message
            messageTime.text = message.timestamp.toString()
//            senderImg.setImageResource(message.senderImg)
            senderName.text = message.senderName
        }
        companion object Factory{
            fun create(parent: ViewGroup): MyMessageHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.message, parent, false)

                return MyMessageHolder(view)
            }
        }
    }

    companion object{
        private const val TYPE_MY = 0
        private const val TYPE_OTHER = 1
    }
}

