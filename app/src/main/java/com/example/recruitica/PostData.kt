package com.example.recruitica

data class PostData(
    val postID: Int = 0,
    val userID:  String = "",
    val description: String? = "",
    val title: String? = "",
    val imageURL: String? = "",
    val likes: Int = 0
)
