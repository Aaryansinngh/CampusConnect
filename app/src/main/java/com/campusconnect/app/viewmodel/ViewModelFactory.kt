package com.campusconnect.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.campusconnect.app.data.repository.AuthRepositoryImpl
import com.campusconnect.app.data.repository.CampusRepositoryImpl

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    private val authRepository by lazy { AuthRepositoryImpl(context.applicationContext) }
    private val campusRepository by lazy { CampusRepositoryImpl(context.applicationContext) }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(authRepository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(campusRepository) as T
            modelClass.isAssignableFrom(ClubViewModel::class.java) -> ClubViewModel(campusRepository) as T
            modelClass.isAssignableFrom(EventViewModel::class.java) -> EventViewModel(campusRepository) as T
            modelClass.isAssignableFrom(PostViewModel::class.java) -> PostViewModel(campusRepository) as T
            modelClass.isAssignableFrom(NotificationViewModel::class.java) -> NotificationViewModel(campusRepository) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(campusRepository) as T
            modelClass.isAssignableFrom(AdminViewModel::class.java) -> AdminViewModel(campusRepository) as T
            modelClass.isAssignableFrom(SuperAdminViewModel::class.java) -> SuperAdminViewModel(campusRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
