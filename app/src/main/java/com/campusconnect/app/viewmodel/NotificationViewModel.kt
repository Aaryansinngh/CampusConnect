package com.campusconnect.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusconnect.app.data.model.NotificationItem
import com.campusconnect.app.data.repository.CampusRepository
import com.campusconnect.app.utils.Resource
import kotlinx.coroutines.launch

class NotificationViewModel(private val repository: CampusRepository) : ViewModel() {
    private val _notificationsState = MutableLiveData<Resource<List<NotificationItem>>>()
    val notificationsState: LiveData<Resource<List<NotificationItem>>> = _notificationsState

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        _notificationsState.value = Resource.Loading()
        viewModelScope.launch {
            _notificationsState.value = repository.getNotifications()
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(notificationId)
            loadNotifications()
        }
    }
}
