package com.campusconnect.app.data.model

data class Post(
    val postId: String = "",
    val clubId: String = "",
    val clubName: String = "",
    val clubLogoUrl: String = "",
    val title: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    var likeCount: Int = 0,
    var commentCount: Int = 0,
    val type: String = TYPE_POST,
    var isLikedByCurrentUser: Boolean = false,
    var isSavedByCurrentUser: Boolean = false
) {
    companion object {
        const val TYPE_POST = "POST"
        const val TYPE_ANNOUNCEMENT = "ANNOUNCEMENT"
    }
}
