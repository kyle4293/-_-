package com.example.familyalbum.chat

import java.util.*

sealed class ChatItem {
    data class MyMessage(
        val message: String?,
        val senderId: String?,
        val timestamp: Date?
    ) : ChatItem()

    data class OtherMessage(
        val message: String?,
        val senderId: String?,
        val senderName: String,
        val timestamp: Date?,
        val senderImg: String
    ) : ChatItem()

    data class DateSeparator(val date: Date) : ChatItem()
}
