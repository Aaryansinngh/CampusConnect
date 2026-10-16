package com.campusconnect.app.data.model

data class Registration(
    val registrationId: String = "",
    val eventId: String = "",
    val eventTitle: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val studentId: String = "",
    val registeredAt: Long = System.currentTimeMillis(),
    val status: String = "CONFIRMED"
)
