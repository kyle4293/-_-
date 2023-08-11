package com.example.familyalbum.timeTable

data class User(
    val email: String,
    val name: String
) {
    constructor() : this( "", "")
}
