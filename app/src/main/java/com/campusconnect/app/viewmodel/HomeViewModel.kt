package com.campusconnect.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusconnect.app.data.model.Event
import com.campusconnect.app.data.model.Post
import com.campusconnect.app.data.repository.CampusRepository
import com.campusconnect.app.utils.Resource
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: CampusRepository) : ViewModel() {
    private val _postsState = MutableLiveData<Resource<List<Post>>>()
    val postsState: LiveData<Resource<List<Post>>> = _postsState

    private val _eventsState = MutableLiveData<Resource<List<Event>>>()
    val eventsState: LiveData<Resource<List<Event>>> = _eventsState

    private val _selectedCategory = MutableLiveData<String>("All")
    val selectedCategory: LiveData<String> = _selectedCategory

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        _postsState.value = Resource.Loading()
        _eventsState.value = Resource.Loading()
        viewModelScope.launch {
            _postsState.value = repository.getPosts(category = _selectedCategory.value)
            _eventsState.value = repository.getEvents(category = _selectedCategory.value)
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        loadHomeData()
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            repository.toggleLikePost(postId)
            loadHomeData()
        }
    }

    fun toggleSave(postId: String) {
        viewModelScope.launch {
            repository.toggleSavePost(postId)
            loadHomeData()
        }
    }
}
