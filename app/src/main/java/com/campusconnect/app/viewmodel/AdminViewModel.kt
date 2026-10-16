package com.campusconnect.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusconnect.app.data.model.Event
import com.campusconnect.app.data.model.Post
import com.campusconnect.app.data.model.Registration
import com.campusconnect.app.data.repository.CampusRepository
import com.campusconnect.app.utils.Resource
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: CampusRepository) : ViewModel() {
    private val _createPostResult = MutableLiveData<Resource<Post>?>()
    val createPostResult: LiveData<Resource<Post>?> = _createPostResult

    private val _createEventResult = MutableLiveData<Resource<Event>?>()
    val createEventResult: LiveData<Resource<Event>?> = _createEventResult

    private val _registrationsState = MutableLiveData<Resource<List<Registration>>>()
    val registrationsState: LiveData<Resource<List<Registration>>> = _registrationsState

    fun createPost(title: String, content: String, imageUrl: String, type: String, clubId: String, clubName: String, clubLogoUrl: String) {
        if (title.isBlank() || content.isBlank()) {
            _createPostResult.value = Resource.Error("Title and Content cannot be empty")
            return
        }
        _createPostResult.value = Resource.Loading()
        viewModelScope.launch {
            val post = Post(
                clubId = clubId.ifEmpty { "club_coding" },
                clubName = clubName.ifEmpty { "MUJ Coding Club" },
                clubLogoUrl = clubLogoUrl,
                title = title,
                content = content,
                imageUrl = imageUrl,
                type = type
            )
            _createPostResult.value = repository.createPost(post)
        }
    }

    fun createEvent(
        title: String, description: String, date: String, startTime: String, endTime: String,
        location: String, deadline: String, capacity: Int, imageUrl: String, clubId: String, clubName: String
    ) {
        if (title.isBlank() || description.isBlank() || location.isBlank() || date.isBlank()) {
            _createEventResult.value = Resource.Error("Please complete all required event fields.")
            return
        }
        if (capacity <= 0) {
            _createEventResult.value = Resource.Error("Capacity must be greater than zero.")
            return
        }

        _createEventResult.value = Resource.Loading()
        viewModelScope.launch {
            val event = Event(
                clubId = clubId.ifEmpty { "club_coding" },
                clubName = clubName.ifEmpty { "MUJ Coding Club" },
                title = title,
                description = description,
                date = date,
                startTime = startTime,
                endTime = endTime,
                location = location,
                registrationDeadline = deadline,
                capacity = capacity,
                imageUrl = imageUrl
            )
            _createEventResult.value = repository.createEvent(event)
        }
    }

    fun loadEventRegistrations(eventId: String) {
        _registrationsState.value = Resource.Loading()
        viewModelScope.launch {
            _registrationsState.value = repository.getEventRegistrations(eventId)
        }
    }

    fun clearResults() {
        _createPostResult.value = null
        _createEventResult.value = null
    }
}
