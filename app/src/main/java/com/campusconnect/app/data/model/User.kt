package com.campusconnect.app.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val role: String = ROLE_STUDENT,
    val studentId: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val ROLE_STUDENT = "STUDENT"
        const val ROLE_CLUB_ADMIN = "CLUB_ADMIN"
        const val ROLE_SUPER_ADMIN = "SUPER_ADMIN"
    }
}
