package com.example.familyalbum.user

data class User(
    val email: String,
    val name: String
) {
    constructor() : this( "", "")
}
