package com.campusconnect.app.data.repository

import android.content.Context
import com.campusconnect.app.data.model.*
import com.campusconnect.app.utils.Resource
import com.campusconnect.app.utils.SampleDataGenerator
import com.campusconnect.app.utils.SessionManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface CampusRepository {
    // Clubs
    suspend fun getClubs(query: String? = null, category: String? = null): Resource<List<Club>>
    suspend fun getClubById(clubId: String): Resource<Club>
    suspend fun toggleFollowClub(clubId: String): Resource<Boolean>
    suspend fun createClub(club: Club): Resource<Club>
    suspend fun approveClub(clubId: String, approve: Boolean): Resource<Boolean>

    // Posts
    suspend fun getPosts(clubId: String? = null, category: String? = null): Resource<List<Post>>
    suspend fun getSavedPosts(): Resource<List<Post>>
    suspend fun getPostById(postId: String): Resource<Post>
    suspend fun createPost(post: Post): Resource<Post>
    suspend fun toggleLikePost(postId: String): Resource<Boolean>
    suspend fun toggleSavePost(postId: String): Resource<Boolean>

    // Comments
    suspend fun getComments(postId: String): Resource<List<Comment>>
    suspend fun addComment(postId: String, text: String): Resource<Comment>
    suspend fun deleteComment(commentId: String): Resource<Boolean>

    // Events
    suspend fun getEvents(category: String? = null, timeFilter: String? = null): Resource<List<Event>>
    suspend fun getEventById(eventId: String): Resource<Event>
    suspend fun createEvent(event: Event): Resource<Event>
    suspend fun registerForEvent(eventId: String): Resource<Registration>
    suspend fun getMyRegistrations(): Resource<List<Registration>>
    suspend fun getEventRegistrations(eventId: String): Resource<List<Registration>>

    // Notifications
    suspend fun getNotifications(): Resource<List<NotificationItem>>
    suspend fun markNotificationAsRead(notificationId: String): Resource<Boolean>

    // Super Admin & User Management
    suspend fun getAllUsers(): Resource<List<User>>
    suspend fun updateUserRole(uid: String, role: String): Resource<Boolean>
    suspend fun deleteContent(type: String, id: String): Resource<Boolean>
}

class CampusRepositoryImpl(private val context: Context) : CampusRepository {
    private val sessionManager = SessionManager(context)
    private val db: FirebaseFirestore? by lazy {
        try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }
    }

    private val followedClubIds = mutableSetOf<String>()
    private val likedPostIds = mutableSetOf<String>()
    private val savedPostIds = mutableSetOf<String>()
    private val registeredEventIds = mutableSetOf<String>()

    override suspend fun getClubs(query: String?, category: String?): Resource<List<Club>> {
        var list = SampleDataGenerator.sampleClubs.toList()
        if (!category.isNullOrBlank() && !category.equals("All", ignoreCase = true)) {
            list = list.filter { it.category.equals(category, ignoreCase = true) }
        }
        if (!query.isNullOrBlank()) {
            list = list.filter { it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
        }
        list = list.map { club -> club.copy(followedByCurrentUser = followedClubIds.contains(club.clubId)) }
        return Resource.Success(list)
    }

    override suspend fun getClubById(clubId: String): Resource<Club> {
        val club = SampleDataGenerator.sampleClubs.find { it.clubId == clubId }
            ?: return Resource.Error("Club not found")
        return Resource.Success(club.copy(followedByCurrentUser = followedClubIds.contains(clubId)))
    }

    override suspend fun toggleFollowClub(clubId: String): Resource<Boolean> {
        val clubIndex = SampleDataGenerator.sampleClubs.indexOfFirst { it.clubId == clubId }
        if (clubIndex != -1) {
            val club = SampleDataGenerator.sampleClubs[clubIndex]
            val isFollowed = followedClubIds.contains(clubId)
            if (isFollowed) {
                followedClubIds.remove(clubId)
                SampleDataGenerator.sampleClubs[clubIndex] = club.copy(followerCount = (club.followerCount - 1).coerceAtLeast(0))
            } else {
                followedClubIds.add(clubId)
                SampleDataGenerator.sampleClubs[clubIndex] = club.copy(followerCount = club.followerCount + 1)
            }
            return Resource.Success(!isFollowed)
        }
        return Resource.Error("Club not found")
    }

    override suspend fun createClub(club: Club): Resource<Club> {
        val newClub = club.copy(clubId = "club_" + System.currentTimeMillis())
        SampleDataGenerator.sampleClubs.add(0, newClub)
        return Resource.Success(newClub)
    }

    override suspend fun approveClub(clubId: String, approve: Boolean): Resource<Boolean> {
        val index = SampleDataGenerator.sampleClubs.indexOfFirst { it.clubId == clubId }
        if (index != -1) {
            SampleDataGenerator.sampleClubs[index] = SampleDataGenerator.sampleClubs[index].copy(approved = approve)
            return Resource.Success(approve)
        }
        return Resource.Error("Club not found")
    }

    override suspend fun getPosts(clubId: String?, category: String?): Resource<List<Post>> {
        var list = SampleDataGenerator.samplePosts.toList()
        if (!clubId.isNullOrBlank()) {
            list = list.filter { it.clubId == clubId }
        }
        list = list.map { post ->
            post.copy(
                isLikedByCurrentUser = likedPostIds.contains(post.postId),
                isSavedByCurrentUser = savedPostIds.contains(post.postId)
            )
        }
        return Resource.Success(list)
    }

    override suspend fun getSavedPosts(): Resource<List<Post>> {
        val list = SampleDataGenerator.samplePosts.filter { savedPostIds.contains(it.postId) }
            .map { it.copy(isLikedByCurrentUser = likedPostIds.contains(it.postId), isSavedByCurrentUser = true) }
        return Resource.Success(list)
    }

    override suspend fun getPostById(postId: String): Resource<Post> {
        val post = SampleDataGenerator.samplePosts.find { it.postId == postId }
            ?: return Resource.Error("Post not found")
        return Resource.Success(post.copy(
            isLikedByCurrentUser = likedPostIds.contains(postId),
            isSavedByCurrentUser = savedPostIds.contains(postId)
        ))
    }

    override suspend fun createPost(post: Post): Resource<Post> {
        val newPost = post.copy(postId = "post_" + System.currentTimeMillis(), createdAt = System.currentTimeMillis())
        SampleDataGenerator.samplePosts.add(0, newPost)
        return Resource.Success(newPost)
    }

    override suspend fun toggleLikePost(postId: String): Resource<Boolean> {
        val index = SampleDataGenerator.samplePosts.indexOfFirst { it.postId == postId }
        if (index != -1) {
            val isLiked = likedPostIds.contains(postId)
            val post = SampleDataGenerator.samplePosts[index]
            if (isLiked) {
                likedPostIds.remove(postId)
                post.likeCount = (post.likeCount - 1).coerceAtLeast(0)
            } else {
                likedPostIds.add(postId)
                post.likeCount += 1
            }
            return Resource.Success(!isLiked)
        }
        return Resource.Error("Post not found")
    }

    override suspend fun toggleSavePost(postId: String): Resource<Boolean> {
        val isSaved = savedPostIds.contains(postId)
        if (isSaved) savedPostIds.remove(postId) else savedPostIds.add(postId)
        return Resource.Success(!isSaved)
    }

    override suspend fun getComments(postId: String): Resource<List<Comment>> {
        val list = SampleDataGenerator.sampleComments.filter { it.postId == postId }
        return Resource.Success(list)
    }

    override suspend fun addComment(postId: String, text: String): Resource<Comment> {
        val currentUser = sessionManager.getUser() ?: return Resource.Error("User not logged in")
        val newComment = Comment(
            commentId = "c_" + System.currentTimeMillis(),
            postId = postId,
            userId = currentUser.uid,
            userName = currentUser.name,
            userProfileImage = currentUser.profileImageUrl,
            text = text,
            createdAt = System.currentTimeMillis()
        )
        SampleDataGenerator.sampleComments.add(newComment)
        val postIndex = SampleDataGenerator.samplePosts.indexOfFirst { it.postId == postId }
        if (postIndex != -1) {
            SampleDataGenerator.samplePosts[postIndex].commentCount += 1
        }
        return Resource.Success(newComment)
    }

    override suspend fun deleteComment(commentId: String): Resource<Boolean> {
        val index = SampleDataGenerator.sampleComments.indexOfFirst { it.commentId == commentId }
        if (index != -1) {
            val comment = SampleDataGenerator.sampleComments.removeAt(index)
            val postIndex = SampleDataGenerator.samplePosts.indexOfFirst { it.postId == comment.postId }
            if (postIndex != -1) {
                SampleDataGenerator.samplePosts[postIndex].commentCount = (SampleDataGenerator.samplePosts[postIndex].commentCount - 1).coerceAtLeast(0)
            }
            return Resource.Success(true)
        }
        return Resource.Error("Comment not found")
    }

    override suspend fun getEvents(category: String?, timeFilter: String?): Resource<List<Event>> {
        var list = SampleDataGenerator.sampleEvents.toList()
        if (!category.isNullOrBlank() && !category.equals("All", ignoreCase = true)) {
            list = list.filter { it.category.equals(category, ignoreCase = true) }
        }
        list = list.map { event -> event.copy(isRegisteredByCurrentUser = registeredEventIds.contains(event.eventId)) }
        return Resource.Success(list)
    }

    override suspend fun getEventById(eventId: String): Resource<Event> {
        val event = SampleDataGenerator.sampleEvents.find { it.eventId == eventId }
            ?: return Resource.Error("Event not found")
        return Resource.Success(event.copy(isRegisteredByCurrentUser = registeredEventIds.contains(eventId)))
    }

    override suspend fun createEvent(event: Event): Resource<Event> {
        val newEvent = event.copy(eventId = "event_" + System.currentTimeMillis())
        SampleDataGenerator.sampleEvents.add(0, newEvent)
        return Resource.Success(newEvent)
    }

    override suspend fun registerForEvent(eventId: String): Resource<Registration> {
        val user = sessionManager.getUser() ?: return Resource.Error("User not logged in")
        val index = SampleDataGenerator.sampleEvents.indexOfFirst { it.eventId == eventId }
        if (index == -1) return Resource.Error("Event not found")

        val event = SampleDataGenerator.sampleEvents[index]
        if (registeredEventIds.contains(eventId)) {
            return Resource.Error("You are already registered for this event.")
        }
        if (event.registrationCount >= event.capacity) {
            return Resource.Error("Event is full.")
        }

        registeredEventIds.add(eventId)
        event.registrationCount += 1

        val registration = Registration(
            registrationId = "reg_" + System.currentTimeMillis(),
            eventId = eventId,
            eventTitle = event.title,
            userId = user.uid,
            userName = user.name,
            userEmail = user.email,
            studentId = user.studentId
        )

        // Add Notification
        SampleDataGenerator.sampleNotifications.add(0, NotificationItem(
            notificationId = "n_" + System.currentTimeMillis(),
            userId = user.uid,
            title = "Registration Confirmed!",
            message = "Your registration for ${event.title} is confirmed.",
            type = NotificationItem.TYPE_REGISTRATION_CONFIRMATION,
            relatedId = eventId,
            isRead = false
        ))

        return Resource.Success(registration)
    }

    override suspend fun getMyRegistrations(): Resource<List<Registration>> {
        val user = sessionManager.getUser() ?: return Resource.Error("User not logged in")
        val registeredEvents = SampleDataGenerator.sampleEvents.filter { registeredEventIds.contains(it.eventId) }
        val regs = registeredEvents.map { event ->
            Registration(
                registrationId = "reg_" + event.eventId,
                eventId = event.eventId,
                eventTitle = event.title,
                userId = user.uid,
                userName = user.name,
                userEmail = user.email,
                studentId = user.studentId
            )
        }
        return Resource.Success(regs)
    }

    override suspend fun getEventRegistrations(eventId: String): Resource<List<Registration>> {
        val event = SampleDataGenerator.sampleEvents.find { it.eventId == eventId }
            ?: return Resource.Error("Event not found")
        val mockRegistrations = listOf(
            Registration("r1", eventId, event.title, "u1", "Alex Johnson", "alex@university.edu", "2026-CS-104"),
            Registration("r2", eventId, event.title, "u2", "David Chen", "david@university.edu", "2025-EE-089"),
            Registration("r3", eventId, event.title, "u3", "Emma Watson", "emma@university.edu", "2026-ME-045")
        )
        return Resource.Success(mockRegistrations)
    }

    override suspend fun getNotifications(): Resource<List<NotificationItem>> {
        return Resource.Success(SampleDataGenerator.sampleNotifications.toList())
    }

    override suspend fun markNotificationAsRead(notificationId: String): Resource<Boolean> {
        val index = SampleDataGenerator.sampleNotifications.indexOfFirst { it.notificationId == notificationId }
        if (index != -1) {
            SampleDataGenerator.sampleNotifications[index].isRead = true
            return Resource.Success(true)
        }
        return Resource.Error("Notification not found")
    }

    override suspend fun getAllUsers(): Resource<List<User>> {
        return Resource.Success(SampleDataGenerator.sampleUsers)
    }

    override suspend fun updateUserRole(uid: String, role: String): Resource<Boolean> {
        val index = SampleDataGenerator.sampleUsers.indexOfFirst { it.uid == uid }
        if (index != -1) {
            SampleDataGenerator.sampleUsers[index] = SampleDataGenerator.sampleUsers[index].copy(role = role)
            return Resource.Success(true)
        }
        return Resource.Error("User not found")
    }

    override suspend fun deleteContent(type: String, id: String): Resource<Boolean> {
        if (type == "POST") {
            SampleDataGenerator.samplePosts.removeAll { it.postId == id }
        } else if (type == "EVENT") {
            SampleDataGenerator.sampleEvents.removeAll { it.eventId == id }
        }
        return Resource.Success(true)
    }
}
