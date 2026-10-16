package com.campusconnect.app.utils

import android.content.Context
import android.content.SharedPreferences
import com.campusconnect.app.data.model.User

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("campus_connect_prefs", Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        prefs.edit().apply {
            putString("KEY_UID", user.uid)
            putString("KEY_NAME", user.name)
            putString("KEY_EMAIL", user.email)
            putString("KEY_ROLE", user.role)
            putString("KEY_STUDENT_ID", user.studentId)
            putString("KEY_PROFILE_IMAGE", user.profileImageUrl)
            putBoolean("KEY_IS_LOGGED_IN", true)
            apply()
        }
    }

    fun getUser(): User? {
        if (!isLoggedIn()) return null
        return User(
            uid = prefs.getString("KEY_UID", "") ?: "",
            name = prefs.getString("KEY_NAME", "Student User") ?: "Student User",
            email = prefs.getString("KEY_EMAIL", "student@university.edu") ?: "student@university.edu",
            role = prefs.getString("KEY_ROLE", User.ROLE_STUDENT) ?: User.ROLE_STUDENT,
            studentId = prefs.getString("KEY_STUDENT_ID", "STU12345") ?: "STU12345",
            profileImageUrl = prefs.getString("KEY_PROFILE_IMAGE", "") ?: ""
        )
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("KEY_IS_LOGGED_IN", false)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}
