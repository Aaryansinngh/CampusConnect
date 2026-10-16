package com.campusconnect.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusconnect.app.data.model.Post
import com.campusconnect.app.data.repository.CampusRepository
import com.campusconnect.app.utils.Resource
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: CampusRepository) : ViewModel() {
    private val _savedPostsState = MutableLiveData<Resource<List<Post>>>()
    val savedPostsState: LiveData<Resource<List<Post>>> = _savedPostsState

    fun loadSavedPosts() {
        _savedPostsState.value = Resource.Loading()
        viewModelScope.launch {
            _savedPostsState.value = repository.getSavedPosts()
        }
    }

    fun unsavePost(postId: String) {
        viewModelScope.launch {
            repository.toggleSavePost(postId)
            loadSavedPosts()
        }
    }
}
