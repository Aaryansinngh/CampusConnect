package com.campusconnect.app.data.model

data class NotificationItem(
    val notificationId: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = TYPE_GENERAL,
    val relatedId: String = "",
    var isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val TYPE_NEW_POST = "NEW_POST"
        const val TYPE_NEW_EVENT = "NEW_EVENT"
        const val TYPE_ANNOUNCEMENT = "ANNOUNCEMENT"
        const val TYPE_REGISTRATION_CONFIRMATION = "REGISTRATION_CONFIRMATION"
        const val TYPE_GENERAL = "GENERAL"
    }
}
