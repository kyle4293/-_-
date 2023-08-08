package com.example.familyalbum

data class Message(
    var message: String?,
    var sendId: String?,
    var sendTime: String?
    ){
    constructor(): this("","","")
}

