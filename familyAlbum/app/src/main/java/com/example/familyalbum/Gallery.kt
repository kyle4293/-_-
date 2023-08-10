package com.example.familyalbum

data class Gallery(
    var imgsrc: String?,
    var date: String
){
    constructor(): this("", "")
}
