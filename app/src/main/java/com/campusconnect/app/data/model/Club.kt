package com.campusconnect.app.data.model

data class Club(
    val clubId: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "Technical",
    val logoUrl: String = "",
    val coverImageUrl: String = "",
    val adminIds: List<String> = emptyList(),
    val followerCount: Int = 0,
    val approved: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val followedByCurrentUser: Boolean = false
)
