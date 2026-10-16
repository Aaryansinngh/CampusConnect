package com.campusconnect.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusconnect.app.data.model.User
import com.campusconnect.app.data.repository.AuthRepository
import com.campusconnect.app.utils.Resource
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _authState = MutableLiveData<Resource<User>>()
    val authState: LiveData<Resource<User>> = _authState

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    init {
        checkCurrentUser()
    }

    fun checkCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = authRepository.getCurrentUser()
        }
    }

    fun login(email: String, pass: String) {
        _authState.value = Resource.Loading()
        viewModelScope.launch {
            val result = authRepository.login(email, pass)
            _authState.value = result
            if (result is Resource.Success) {
                _currentUser.value = result.data
            }
        }
    }

    fun signup(name: String, email: String, pass: String, studentId: String) {
        _authState.value = Resource.Loading()
        viewModelScope.launch {
            val result = authRepository.signup(name, email, pass, studentId)
            _authState.value = result
            if (result is Resource.Success) {
                _currentUser.value = result.data
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _currentUser.value = null
        }
    }

    fun switchDemoRole(role: String) {
        viewModelScope.launch {
            val user = authRepository.switchDemoRole(role)
            _currentUser.value = user
            _authState.value = Resource.Success(user)
        }
    }
}
