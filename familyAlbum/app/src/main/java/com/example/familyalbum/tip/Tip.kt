package com.example.familyalbum.tip

data class Tip(
    var title: String,
    var tag: String,
    var contents: List<Content>?
){
    constructor() : this("", "", null)
}

