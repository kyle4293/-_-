package com.example.familyalbum.home

class Folder(
    val id: String,
    val name: String,
    val description: String,
    val imageList: List<ImageInfo> // Use List<ImageInfo> instead of Map<String, String>
)
