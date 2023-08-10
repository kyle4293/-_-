package com.example.familyalbum.tip

data class Tip(var title: String?,
               var content: String?,
               var heart: Int?
){
    constructor(): this("","",0)
}

