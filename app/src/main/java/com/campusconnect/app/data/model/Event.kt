package com.campusconnect.app.data.model

data class Event(
    val eventId: String = "",
    val clubId: String = "",
    val clubName: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val location: String = "",
    val registrationDeadline: String = "",
    val capacity: Int = 100,
    var registrationCount: Int = 0,
    val category: String = "Technical",
    val createdAt: Long = System.currentTimeMillis(),
    var isRegisteredByCurrentUser: Boolean = false
)
