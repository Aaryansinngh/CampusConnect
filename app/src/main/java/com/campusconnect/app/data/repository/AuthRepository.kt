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
    suspend fun signup(
        name: String,
        email: String,
        pass: String,
        studentId: String
    ): Resource<User>

    suspend fun getCurrentUser(): User?
    suspend fun logout()
    suspend fun switchDemoRole(role: String): User
}

class AuthRepositoryImpl(context: Context) : AuthRepository {

    private val sessionManager = SessionManager(context.applicationContext)
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override suspend fun login(email: String, pass: String): Resource<User> {
        if (email.isBlank() || pass.isBlank()) {
            return Resource.Error("Email and password cannot be empty.")
        }

        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), pass).await()
            val firebaseUser = result.user
                ?: return Resource.Error("Firebase authentication failed.")

            val uid = firebaseUser.uid
            val userRef = db.collection("users").document(uid)
            val snapshot = userRef.get().await()

            val user = if (snapshot.exists()) {
                snapshot.toObject(User::class.java)?.copy(
                    uid = uid,
                    email = firebaseUser.email ?: email.trim()
                ) ?: return Resource.Error("Your profile data is invalid.")
            } else {
                // Create a basic profile for an authenticated account
                // that does not yet have a Firestore profile.
                User(
                    uid = uid,
                    name = firebaseUser.displayName
                        ?: firebaseUser.email?.substringBefore("@").orEmpty(),
                    email = firebaseUser.email ?: email.trim(),
                    role = User.ROLE_STUDENT,
                    studentId = ""
                ).also { profile ->
                    userRef.set(profile).await()
                }
            }

            sessionManager.saveUser(user)
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to sign in. Please check your credentials and connection.")
        }
    }

    override suspend fun signup(
        name: String,
        email: String,
        pass: String,
        studentId: String
    ): Resource<User> {
        if (name.isBlank() || email.isBlank() || pass.isBlank() || studentId.isBlank()) {
            return Resource.Error("Please fill out all required fields.")
        }

        if (pass.length < 6) {
            return Resource.Error("Password must be at least 6 characters.")
        }

        return try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), pass).await()
            val firebaseUser = result.user
                ?: return Resource.Error("Firebase could not create your account.")

            val user = User(
                uid = firebaseUser.uid,
                name = name.trim(),
                email = firebaseUser.email ?: email.trim(),
                role = User.ROLE_STUDENT,
                studentId = studentId.trim()
            )

            // Save the profile before reporting signup success.
            db.collection("users")
                .document(firebaseUser.uid)
                .set(user)
                .await()

            sessionManager.saveUser(user)
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create your account. Please try again.")
        }
    }

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: run {
            sessionManager.logout()
            return null
        }

        return try {
            val snapshot = db.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            val user = snapshot.toObject(User::class.java)
                ?: sessionManager.getUser()?.takeIf { it.uid == firebaseUser.uid }
                ?: User(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName
                        ?: firebaseUser.email?.substringBefore("@").orEmpty(),
                    email = firebaseUser.email.orEmpty(),
                    role = User.ROLE_STUDENT,
                    studentId = ""
                )

            sessionManager.saveUser(user)
            user
        } catch (_: Exception) {
            // Keep the cached profile available during a temporary network outage,
            // but only when a matching Firebase session still exists.
            sessionManager.getUser()?.takeIf { it.uid == firebaseUser.uid }
        }
    }

    override suspend fun logout() {
        auth.signOut()
        sessionManager.logout()
    }

    override suspend fun switchDemoRole(role: String): User {
        // Explicit demo mode only; this does not authenticate a Firebase account.
        val targetUser = when (role) {
            User.ROLE_CLUB_ADMIN -> SampleDataGenerator.sampleUsers[1]
            User.ROLE_SUPER_ADMIN -> SampleDataGenerator.sampleUsers[2]
            else -> SampleDataGenerator.sampleUsers[0]
        }

        sessionManager.saveUser(targetUser)
        return targetUser
    }
}
