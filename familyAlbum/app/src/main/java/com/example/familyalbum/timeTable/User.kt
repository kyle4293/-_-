package com.example.familyalbum.timeTable

data class User(
    val email: String,
    val name: String,
    val profileImageUrl: String?,
    val groupId: String?
) {
    constructor() : this( "", "", "", "")
}
