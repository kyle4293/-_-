package com.example.familyalbum.home

data class Gallery(
    var imgsrc: String?,
    var date: String
){
    constructor(): this("", "")
}
