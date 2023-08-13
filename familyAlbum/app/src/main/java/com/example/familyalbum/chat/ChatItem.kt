package com.example.familyalbum.chat

import java.util.*

interface ChatItem {

    data class MyMessage(
        var message: String?,
        var senderId: String?,
        var timestamp: Date?
        ): ChatItem

    data class OtherMessage(
        var message: String?,
        var senderId: String?,
        var senderName: String,
        var timestamp: Date?,
        var senderImg: String
    ): ChatItem

    data class DateSeparator(var date: Date) : ChatItem

}