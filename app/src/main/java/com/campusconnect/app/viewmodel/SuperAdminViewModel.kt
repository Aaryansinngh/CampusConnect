package com.campusconnect.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusconnect.app.data.model.Club
import com.campusconnect.app.data.model.User
import com.campusconnect.app.data.repository.CampusRepository
import com.campusconnect.app.utils.Resource
import kotlinx.coroutines.launch

class SuperAdminViewModel(private val repository: CampusRepository) : ViewModel() {
    private val _usersState = MutableLiveData<Resource<List<User>>>()
    val usersState: LiveData<Resource<List<User>>> = _usersState

    private val _clubsState = MutableLiveData<Resource<List<Club>>>()
    val clubsState: LiveData<Resource<List<Club>>> = _clubsState

    init {
        loadData()
    }

    fun loadData() {
        _usersState.value = Resource.Loading()
        _clubsState.value = Resource.Loading()
        viewModelScope.launch {
            _usersState.value = repository.getAllUsers()
            _clubsState.value = repository.getClubs()
        }
    }

    fun updateUserRole(uid: String, role: String) {
        viewModelScope.launch {
            repository.updateUserRole(uid, role)
            loadData()
        }
    }

    fun approveClub(clubId: String, approve: Boolean) {
        viewModelScope.launch {
            repository.approveClub(clubId, approve)
            loadData()
        }
    }

    fun deleteContent(type: String, id: String) {
        viewModelScope.launch {
            repository.deleteContent(type, id)
            loadData()
        }
    }
}
