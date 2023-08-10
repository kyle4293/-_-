package com.example.familyalbum

data class Tip(var title: String?,
               var content: String?,
               var user: String?,
               var tag: String?
){
    constructor(): this("","","","")
}

