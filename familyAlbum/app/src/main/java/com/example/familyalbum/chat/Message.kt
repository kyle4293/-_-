package com.example.familyalbum.chat

import java.util.Date

data class Message(
    var message: String?,
    var senderId: String?,
    var timestamp: Date?
) {
    constructor() : this("", "", null)
}
