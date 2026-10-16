package com.campusconnect.app.data.model

data class Comment(
    val commentId: String = "",
    val postId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userProfileImage: String = "",
    val text: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
