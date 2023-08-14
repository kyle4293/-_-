package com.example.familyalbum.chat

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.familyalbum.R
import com.example.familyalbum.databinding.DateSeparatorBinding
import com.example.familyalbum.databinding.MessageBinding
import com.example.familyalbum.databinding.MymessageBinding
import kotlinx.coroutines.NonDisposableHandle.parent
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MessageAdapter(val messageList: ArrayList<ChatItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    inner class ViewHolder(val binding: MessageBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_MY -> MyMessageHolder.create(parent)
            TYPE_OTHER -> OtherMessageHolder.create(parent)
            TYPE_DATE_SEPARATOR -> DateSeparatorHolder.create(parent)
            else -> throw IllegalStateException("Not Found ViewHolder Type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyMessageHolder -> {
                holder.bind(messageList[position] as ChatItem.MyMessage)
            }
            is OtherMessageHolder -> {
                holder.bind(messageList[position] as ChatItem.OtherMessage)
            }
            is DateSeparatorHolder -> {
                holder.bindDateSeparator(messageList[position] as ChatItem.DateSeparator)
            }
        }
    }

    override fun getItemViewType(position: Int) = when (messageList[position]) {
        is ChatItem.MyMessage -> {
            TYPE_MY
        }
        is ChatItem.OtherMessage -> {
            TYPE_OTHER
        }
        is ChatItem.DateSeparator -> {
            TYPE_DATE_SEPARATOR
        }
        else -> {
            throw IllegalStateException("Not Found ViewHolder Type")
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    private fun addDateSeparators() {

        val newList = mutableListOf<ChatItem>()
        var currentDate: Date? = null
        Log.e(TAG, messageList.toString())

        for (item in messageList) {
            if (item is ChatItem.MyMessage || item is ChatItem.OtherMessage) {
                val itemDate = (item as? ChatItem.MyMessage)?.timestamp ?: (item as? ChatItem.OtherMessage)?.timestamp

                if (itemDate != null && !isSameDate(itemDate, currentDate)) {
                    currentDate = itemDate
                    newList.add(ChatItem.DateSeparator(itemDate))
                }
            }
            newList.add(item)
        }

        messageList.clear()
        messageList.addAll(newList)
    }

    private fun isSameDate(date1: Date, date2: Date?): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate1 = sdf.format(date1)
        val formattedDate2 = date2?.let { sdf.format(it) }
        Log.e("testing", "same DAte!!!")
        return formattedDate1 == formattedDate2
    }

    fun updateMessageList(newMessageList: List<ChatItem>) {
        messageList.clear()
        messageList.addAll(newMessageList)
        addDateSeparators() // 날짜 분리선 추가
        notifyDataSetChanged()
    }

    class DateSeparatorHolder(private val binding: DateSeparatorBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindDateSeparator(item: ChatItem.DateSeparator) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = sdf.format(item.date)
            binding.dateTextView.text = formattedDate
        }

        companion object {
            fun create(parent: ViewGroup): DateSeparatorHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DateSeparatorBinding.inflate(layoutInflater, parent, false)
                return DateSeparatorHolder(binding)
            }
        }
    }

    class MyMessageHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){
        private val messageText = itemView.findViewById<TextView>(R.id.send_message_text)
        private val messageTime = itemView.findViewById<TextView>(R.id.send_message_time)

        fun bind(message: ChatItem.MyMessage){

            messageText.text = message.message
            val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(message.timestamp)
            messageTime.text = formattedTime.toString()
        }


        companion object Factory {
            fun create(parent: ViewGroup): MyMessageHolder {
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
            val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(message.timestamp)

            messageTime.text = formattedTime.toString()
            senderName.text = message.senderName
            Log.e("test", message.senderName)
            val imageUri = Uri.parse(message.senderImg)

            Glide.with(itemView.context)
                .load(imageUri)
                .circleCrop()
                .into(senderImg)
        }


        companion object Factory {
            fun create(parent: ViewGroup): OtherMessageHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.message, parent, false)

                return OtherMessageHolder(view)
            }
        }
    }

    companion object{
        private const val TYPE_MY = 0
        private const val TYPE_OTHER = 1
        private const val TYPE_DATE_SEPARATOR = 2
    }
}