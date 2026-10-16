package com.campusconnect.app.data.repository

import android.content.Context
import com.campusconnect.app.data.model.User
import com.campusconnect.app.utils.Resource
import com.campusconnect.app.utils.SampleDataGenerator
import com.campusconnect.app.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface AuthRepository {
    suspend fun login(email: String, pass: String): Resource<User>
    suspend fun signup(name: String, email: String, pass: String, studentId: String): Resource<User>
    suspend fun getCurrentUser(): User?
    suspend fun logout()
    suspend fun switchDemoRole(role: String): User
}

class AuthRepositoryImpl(private val context: Context) : AuthRepository {
    private val sessionManager = SessionManager(context)
    private val auth: FirebaseAuth? by lazy {
        try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
    }
    private val db: FirebaseFirestore? by lazy {
        try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }
    }

    override suspend fun login(email: String, pass: String): Resource<User> {
        if (email.isBlank() || pass.isBlank()) {
            return Resource.Error("Email and password cannot be empty")
        }
        
        // Try Firebase Auth if available
        if (auth != null && db != null) {
            try {
                val result = auth!!.signInWithEmailAndPassword(email, pass).await()
                val uid = result.user?.uid ?: return Resource.Error("Authentication failed")
                val doc = db!!.collection("users").document(uid).get().await()
                val user = doc.toObject(User::class.java) ?: User(uid = uid, email = email, name = email.substringBefore("@"))
                sessionManager.saveUser(user)
                return Resource.Success(user)
            } catch (e: Exception) {
                // Fallback to sample users if Firebase auth fails or offline
            }
        }

        // Offline / Seed Fallback matching
        val matchedUser = SampleDataGenerator.sampleUsers.find { it.email.equals(email, ignoreCase = true) }
            ?: User(
                uid = "user_" + System.currentTimeMillis(),
                name = email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() },
                email = email,
                role = User.ROLE_STUDENT,
                studentId = "2026-STU-99"
            )

        sessionManager.saveUser(matchedUser)
        return Resource.Success(matchedUser)
    }

    override suspend fun signup(name: String, email: String, pass: String, studentId: String): Resource<User> {
        if (name.isBlank() || email.isBlank() || pass.isBlank() || studentId.isBlank()) {
            return Resource.Error("Please fill out all required fields")
        }
        if (pass.length < 6) {
            return Resource.Error("Password must be at least 6 characters")
        }

        val newUser = User(
            uid = "user_" + System.currentTimeMillis(),
            name = name,
            email = email,
            role = User.ROLE_STUDENT,
            studentId = studentId
        )

        if (auth != null && db != null) {
            try {
                val result = auth!!.createUserWithEmailAndPassword(email, pass).await()
                val uid = result.user?.uid ?: newUser.uid
                val firebaseUser = newUser.copy(uid = uid)
                db!!.collection("users").document(uid).set(firebaseUser).await()
                sessionManager.saveUser(firebaseUser)
                return Resource.Success(firebaseUser)
            } catch (e: Exception) {
                // Fallback to local session
            }
        }

        sessionManager.saveUser(newUser)
        return Resource.Success(newUser)
    }

    override suspend fun getCurrentUser(): User? {
        return sessionManager.getUser()
    }

    override suspend fun logout() {
        try { auth?.signOut() } catch (e: Exception) {}
        sessionManager.logout()
    }

    override suspend fun switchDemoRole(role: String): User {
        val targetUser = when (role) {
            User.ROLE_CLUB_ADMIN -> SampleDataGenerator.sampleUsers[1]
            User.ROLE_SUPER_ADMIN -> SampleDataGenerator.sampleUsers[2]
            else -> SampleDataGenerator.sampleUsers[0]
        }
        sessionManager.saveUser(targetUser)
        return targetUser
    }
}
