package com.campusconnect.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusconnect.app.data.model.Comment
import com.campusconnect.app.data.model.Post
import com.campusconnect.app.data.repository.CampusRepository
import com.campusconnect.app.utils.Resource
import kotlinx.coroutines.launch

class PostViewModel(private val repository: CampusRepository) : ViewModel() {
    private val _postDetailState = MutableLiveData<Resource<Post>>()
    val postDetailState: LiveData<Resource<Post>> = _postDetailState

    private val _commentsState = MutableLiveData<Resource<List<Comment>>>()
    val commentsState: LiveData<Resource<List<Comment>>> = _commentsState

    private val _addCommentState = MutableLiveData<Resource<Comment>?>()
    val addCommentState: LiveData<Resource<Comment>?> = _addCommentState

    fun loadPostDetail(postId: String) {
        _postDetailState.value = Resource.Loading()
        _commentsState.value = Resource.Loading()
        viewModelScope.launch {
            _postDetailState.value = repository.getPostById(postId)
            _commentsState.value = repository.getComments(postId)
        }
    }

    fun addComment(postId: String, text: String) {
        if (text.isBlank()) return
        _addCommentState.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.addComment(postId, text)
            _addCommentState.value = result
            if (result is Resource.Success) {
                _commentsState.value = repository.getComments(postId)
                _postDetailState.value = repository.getPostById(postId)
            }
        }
    }

    fun deleteComment(commentId: String, postId: String) {
        viewModelScope.launch {
            repository.deleteComment(commentId)
            _commentsState.value = repository.getComments(postId)
            _postDetailState.value = repository.getPostById(postId)
        }
    }

    fun clearAddCommentState() {
        _addCommentState.value = null
    }
}
