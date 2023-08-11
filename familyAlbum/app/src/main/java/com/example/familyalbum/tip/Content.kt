package com.example.familyalbum.tip

data class Content(
    var tipId: String,
    var content: String
){
    constructor(): this("","")
}
