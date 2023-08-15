package com.example.familyalbum.tip

data class Tip(
    var title: String,
    var tag: String,
    var content: String?,
    var groupId: String
){
    constructor() : this("", "", null,"")
}

