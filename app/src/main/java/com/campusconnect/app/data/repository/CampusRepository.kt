package com.campusconnect.app.data.repository


import android.content.Context
import com.campusconnect.app.data.model.*
import com.campusconnect.app.utils.Resource
import com.campusconnect.app.utils.SampleDataGenerator
import com.campusconnect.app.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
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
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val postsCollection
        get() = requireNotNull(db) { "Firebase Firestore is unavailable" }
            .collection("posts")

    private val commentsCollection
        get() = requireNotNull(db) { "Firebase Firestore is unavailable" }
            .collection("comments")

    private fun currentUserId(): String? = auth.currentUser?.uid

    private val followedClubIds = mutableSetOf<String>()
    private val likedPostIds = mutableSetOf<String>()
    private val savedPostIds = mutableSetOf<String>()
    private val registeredEventIds = mutableSetOf<String>()



    override suspend fun getClubs(
        query: String?,
        category: String?
    ): Resource<List<Club>> {

        // Always start with the existing sample clubs.
        var list = SampleDataGenerator.sampleClubs.toList()

        // Apply filters without depending on Firebase.
        if (!category.isNullOrBlank() &&
            !category.equals("All", ignoreCase = true)
        ) {
            list = list.filter {
                it.category.equals(category, ignoreCase = true)
            }
        }

        if (!query.isNullOrBlank()) {
            list = list.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }

        // Restore follow status from Firebase when available.
        val userId = auth.currentUser?.uid

        if (userId != null) {
            try {
                val firestore = requireNotNull(db) {
                    "Firebase Firestore is unavailable."
                }

                val snapshot = firestore.collection("users")
                    .document(userId)
                    .collection("followedClubs")
                    .get()
                    .await()

                followedClubIds.clear()
                followedClubIds.addAll(
                    snapshot.documents.map { it.id }
                )

                android.util.Log.d(
                    "CampusConnect",
                    "Loaded ${followedClubIds.size} followed clubs."
                )

            } catch (e: Exception) {
                android.util.Log.e(
                    "CampusConnect",
                    "Could not load followed clubs; displaying clubs anyway.",
                    e
                )
            }
        }

        // Return the clubs even if loading follow status failed.
        val result = list.map { club ->
            club.copy(
                followedByCurrentUser =
                    followedClubIds.contains(club.clubId)
            )
        }

        return Resource.Success(result)
    }






    override suspend fun getClubById(clubId: String): Resource<Club> {
        return try {
            val club = SampleDataGenerator.sampleClubs
                .find { it.clubId == clubId }
                ?: return Resource.Error("Club not found.")

            val userId = auth.currentUser?.uid

            val isFollowed = if (userId != null) {
                val firestore = requireNotNull(db) {
                    "Firebase Firestore is unavailable."
                }

                firestore.collection("users")
                    .document(userId)
                    .collection("followedClubs")
                    .document(clubId)
                    .get()
                    .await()
                    .exists()
            } else {
                false
            }

            if (isFollowed) {
                followedClubIds.add(clubId)
            } else {
                followedClubIds.remove(clubId)
            }

            Resource.Success(
                club.copy(followedByCurrentUser = isFollowed)
            )
        } catch (e: Exception) {
            android.util.Log.e(
                "CampusConnect",
                "Failed to load club: $clubId",
                e
            )
            Resource.Error(e.message ?: "Unable to load club.")
        }
    }


    override suspend fun toggleFollowClub(clubId: String): Resource<Boolean> {
        return try {
            val firebaseUser = auth.currentUser
                ?: return Resource.Error("Please log in to follow a club.")

            val firestore = requireNotNull(db) {
                "Firebase Firestore is unavailable."
            }

            val club = SampleDataGenerator.sampleClubs
                .find { it.clubId == clubId }
                ?: return Resource.Error("Club not found.")

            val userId = firebaseUser.uid

            val followRef = firestore.collection("users")
                .document(userId)
                .collection("followedClubs")
                .document(clubId)

            val snapshot = followRef.get().await()

            if (snapshot.exists()) {
                followRef.delete().await()
                followedClubIds.remove(clubId)

                android.util.Log.d(
                    "CampusConnect",
                    "UNFOLLOW SUCCESS: ${followRef.path}"
                )

                Resource.Success(false)
            } else {
                val followData = hashMapOf(
                    "clubId" to club.clubId,
                    "clubName" to club.name,
                    "userId" to userId,
                    "followedAt" to System.currentTimeMillis()
                )

                followRef.set(followData).await()
                followedClubIds.add(clubId)

                android.util.Log.d(
                    "CampusConnect",
                    "FOLLOW SUCCESS: ${followRef.path}"
                )

                Resource.Success(true)
            }
        } catch (e: Exception) {
            android.util.Log.e(
                "CampusConnect",
                "FOLLOW CLUB FAILED: $clubId",
                e
            )

            Resource.Error(
                e.message ?: "Unable to update club follow status."
            )
        }
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

    override suspend fun getPosts(
        clubId: String?,
        category: String?
    ): Resource<List<Post>> {
        return try {
            val userId = currentUserId()
                ?: return Resource.Error("Please log in to view posts.")

            var query = postsCollection.orderBy(
                "createdAt",
                com.google.firebase.firestore.Query.Direction.DESCENDING
            )

            if (!clubId.isNullOrBlank()) {
                query = query.whereEqualTo("clubId", clubId)
            }

            val snapshot = query.get().await()

            val posts = snapshot.documents.mapNotNull { document ->
                val post = document.toObject(Post::class.java)
                    ?: return@mapNotNull null

                val liked = document.reference
                    .collection("likes")
                    .document(userId)
                    .get()
                    .await()
                    .exists()

                val saved = db!!
                    .collection("users")
                    .document(userId)
                    .collection("savedPosts")
                    .document(document.id)
                    .get()
                    .await()
                    .exists()

                post.copy(
                    postId = document.id,
                    isLikedByCurrentUser = liked,
                    isSavedByCurrentUser = saved
                )
            }

            Resource.Success(posts)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to load posts.")
        }
    }

    override suspend fun getSavedPosts(): Resource<List<Post>> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Resource.Error("Please log in to view saved posts.")

            val dbInstance = requireNotNull(db) {
                "Firebase Firestore is unavailable"
            }

            val savedSnapshot = dbInstance
                .collection("users")
                .document(userId)
                .collection("savedPosts")
                .get()
                .await()

            val posts = savedSnapshot.documents.mapNotNull { savedDocument ->
                val postId = savedDocument.getString("postId")
                    ?: savedDocument.id

                val postSnapshot = postsCollection
                    .document(postId)
                    .get()
                    .await()

                if (!postSnapshot.exists()) {
                    return@mapNotNull null
                }

                val post = postSnapshot.toObject(Post::class.java)
                    ?: return@mapNotNull null

                val isLiked = postSnapshot.reference
                    .collection("likes")
                    .document(userId)
                    .get()
                    .await()
                    .exists()

                post.copy(
                    postId = postSnapshot.id,
                    isLikedByCurrentUser = isLiked,
                    isSavedByCurrentUser = true
                )
            }.sortedByDescending { it.createdAt }

            Resource.Success(posts)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to load saved posts.")
        }
    }



    override suspend fun getPostById(postId: String): Resource<Post> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Resource.Error("Please log in to view this post.")

            val document = postsCollection
                .document(postId)
                .get()
                .await()

            if (!document.exists()) {
                return Resource.Error("Post not found.")
            }

            val post = document.toObject(Post::class.java)
                ?: return Resource.Error("Unable to read post data.")

            val isLiked = document.reference
                .collection("likes")
                .document(userId)
                .get()
                .await()
                .exists()

            val isSaved = requireNotNull(db)
                .collection("users")
                .document(userId)
                .collection("savedPosts")
                .document(postId)
                .get()
                .await()
                .exists()

            Resource.Success(
                post.copy(
                    postId = document.id,
                    isLikedByCurrentUser = isLiked,
                    isSavedByCurrentUser = isSaved
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to load post.")
        }
    }



    override suspend fun createPost(post: Post): Resource<Post> {
        return try {
            val firebaseUser = auth.currentUser
                ?: return Resource.Error("Please log in to create a post.")

            val userSnapshot = requireNotNull(db)
                .collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            val authorName = userSnapshot.getString("name")
                ?: firebaseUser.displayName
                ?: firebaseUser.email
                ?: "CampusConnect User"

            val document = postsCollection.document()

            val newPost = post.copy(
                postId = document.id,
                authorId = firebaseUser.uid,
                authorName = authorName,
                createdAt = System.currentTimeMillis(),
                likeCount = 0,
                commentCount = 0,
                isLikedByCurrentUser = false,
                isSavedByCurrentUser = false
            )

            document.set(newPost).await()

            Resource.Success(newPost)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create post.")
        }
    }

    override suspend fun toggleLikePost(postId: String): Resource<Boolean> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Resource.Error("Please log in to like posts.")

            val postRef = postsCollection.document(postId)
            val likeRef = postRef.collection("likes").document(userId)

            val dbInstance = requireNotNull(db) {
                "Firebase Firestore is unavailable"
            }

            val newLikeState = dbInstance.runTransaction { transaction ->
                val postSnapshot = transaction.get(postRef)

                if (!postSnapshot.exists()) {
                    throw IllegalStateException("Post not found.")
                }

                val likeSnapshot = transaction.get(likeRef)
                val currentLikeCount =
                    (postSnapshot.getLong("likeCount") ?: 0L).toInt()

                if (likeSnapshot.exists()) {
                    transaction.delete(likeRef)
                    transaction.update(
                        postRef,
                        "likeCount",
                        (currentLikeCount - 1).coerceAtLeast(0)
                    )
                    false
                } else {
                    transaction.set(
                        likeRef,
                        mapOf(
                            "userId" to userId,
                            "createdAt" to System.currentTimeMillis()
                        )
                    )
                    transaction.update(
                        postRef,
                        "likeCount",
                        currentLikeCount + 1
                    )
                    true
                }
            }.await()

            Resource.Success(newLikeState)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update post like.")
        }
    }




    override suspend fun toggleSavePost(postId: String): Resource<Boolean> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Resource.Error("Please log in to save posts.")

            val dbInstance = requireNotNull(db) {
                "Firebase Firestore is unavailable"
            }

            val postRef = postsCollection.document(postId)
            val savedRef = dbInstance
                .collection("users")
                .document(userId)
                .collection("savedPosts")
                .document(postId)

            val postSnapshot = postRef.get().await()

            if (!postSnapshot.exists()) {
                return Resource.Error("Post not found.")
            }

            val savedSnapshot = savedRef.get().await()

            if (savedSnapshot.exists()) {
                savedRef.delete().await()
                Resource.Success(false)
            } else {
                savedRef.set(
                    mapOf(
                        "postId" to postId,
                        "savedAt" to System.currentTimeMillis()
                    )
                ).await()

                Resource.Success(true)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update saved post.")
        }
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
        return try {
            val firebaseUser = auth.currentUser
                ?: return Resource.Error("Please log in to register for an event.")

            val firestore = requireNotNull(db) {
                "Firestore is not initialized."
            }

            // Find the event in the existing sample data.
            val event = SampleDataGenerator.sampleEvents
                .find { it.eventId == eventId }
                ?: return Resource.Error("Event not found.")

            val userId = firebaseUser.uid

            // Retrieve the user's profile from Firestore.
            val userSnapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (!userSnapshot.exists()) {
                return Resource.Error("User profile not found.")
            }

            val userName = userSnapshot.getString("name")
                ?: firebaseUser.displayName
                ?: "CampusConnect User"

            val userEmail = firebaseUser.email.orEmpty()
            val studentId = userSnapshot.getString("studentId").orEmpty()

            // Create a deterministic document ID to prevent duplicate registrations.
            val registrationId = "${eventId}_${userId}"
            val registrationRef = firestore.collection("registrations")
                .document(registrationId)

            // Atomically check for an existing registration and create one if absent.
            val registration = firestore.runTransaction { transaction ->
                val existingRegistration = transaction.get(registrationRef)

                if (existingRegistration.exists()) {
                    throw IllegalStateException(
                        "You are already registered for this event."
                    )
                }

                val newRegistration = Registration(
                    registrationId = registrationId,
                    eventId = event.eventId,
                    eventTitle = event.title,
                    userId = userId,
                    userName = userName,
                    userEmail = userEmail,
                    studentId = studentId,
                    registeredAt = System.currentTimeMillis(),
                    status = "CONFIRMED"
                )

                transaction.set(registrationRef, newRegistration)
                newRegistration
            }.await()

            // Update the in-memory UI state only after Firestore succeeds.
            if (!registeredEventIds.contains(eventId)) {
                registeredEventIds.add(eventId)
                event.registrationCount += 1
            }

            // Preserve the existing local notification behavior.
            SampleDataGenerator.sampleNotifications.add(
                0,
                NotificationItem(
                    notificationId = "n_${System.currentTimeMillis()}",
                    userId = userId,
                    title = "Registration Confirmed!",
                    message = "Your registration for ${event.title} is confirmed.",
                    type = NotificationItem.TYPE_REGISTRATION_CONFIRMATION,
                    relatedId = eventId,
                    isRead = false
                )
            )

            android.util.Log.d(
                "CampusConnect",
                "Registration saved successfully: $registrationId"
            )

            Resource.Success(registration)

        } catch (e: Exception) {
            android.util.Log.e(
                "CampusConnect",
                "Failed to register for event: $eventId",
                e
            )

            val message = when {
                e.message?.contains(
                    "already registered",
                    ignoreCase = true
                ) == true -> "You are already registered for this event."

                else -> e.message
                    ?: "Unable to register for this event. Check the application logs."
            }

            Resource.Error(message)
        }
    }





    override suspend fun getMyRegistrations(): Resource<List<Registration>> {
        return try {
            val firebaseUser = auth.currentUser
                ?: return Resource.Error("Please log in to view your registrations.")

            val firestore = requireNotNull(db) {
                "Firestore is not initialized."
            }

            val snapshot = firestore.collection("registrations")
                .whereEqualTo("userId", firebaseUser.uid)
                .get()
                .await()

            val registrations = snapshot.documents.mapNotNull { document ->
                document.toObject(Registration::class.java)
            }.sortedByDescending { it.registeredAt }

            Resource.Success(registrations)
        } catch (e: Exception) {
            Resource.Error(
                e.message ?: "Unable to load your registrations."
            )
        }
    }

    override suspend fun getEventRegistrations(
        eventId: String
    ): Resource<List<Registration>> {
        return try {
            val firebaseUser = auth.currentUser
                ?: return Resource.Error("Please log in to view event registrations.")

            val firestore = requireNotNull(db) {
                "Firestore is not initialized."
            }

            // Confirm that the event exists in the current sample data.
            val eventExists = SampleDataGenerator.sampleEvents.any {
                it.eventId == eventId
            }

            if (!eventExists) {
                return Resource.Error("Event not found.")
            }

            // Load actual registrations for this event.
            val snapshot = firestore.collection("registrations")
                .whereEqualTo("eventId", eventId)
                .get()
                .await()

            val registrations = snapshot.documents.mapNotNull { document ->
                document.toObject(Registration::class.java)
            }.sortedByDescending { it.registeredAt }

            Resource.Success(registrations)
        } catch (e: Exception) {
            Resource.Error(
                e.message ?: "Unable to load event registrations."
            )
        }
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
