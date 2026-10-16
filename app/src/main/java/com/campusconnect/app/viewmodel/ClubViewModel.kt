package com.campusconnect.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusconnect.app.data.model.Club
import com.campusconnect.app.data.model.Event
import com.campusconnect.app.data.model.Post
import com.campusconnect.app.data.repository.CampusRepository
import com.campusconnect.app.utils.Resource
import kotlinx.coroutines.launch

class ClubViewModel(private val repository: CampusRepository) : ViewModel() {
    private val _clubsState = MutableLiveData<Resource<List<Club>>>()
    val clubsState: LiveData<Resource<List<Club>>> = _clubsState

    private val _selectedClubState = MutableLiveData<Resource<Club>>()
    val selectedClubState: LiveData<Resource<Club>> = _selectedClubState

    private val _clubPostsState = MutableLiveData<Resource<List<Post>>>()
    val clubPostsState: LiveData<Resource<List<Post>>> = _clubPostsState

    private val _clubEventsState = MutableLiveData<Resource<List<Event>>>()
    val clubEventsState: LiveData<Resource<List<Event>>> = _clubEventsState

    private val _searchQuery = MutableLiveData<String>("")
    private val _selectedCategory = MutableLiveData<String>("All")

    init {
        loadClubs()
    }

    fun loadClubs() {
        _clubsState.value = Resource.Loading()
        viewModelScope.launch {
            _clubsState.value = repository.getClubs(_searchQuery.value, _selectedCategory.value)
        }
    }

    fun searchClubs(query: String) {
        _searchQuery.value = query
        loadClubs()
    }

    fun filterCategory(category: String) {
        _selectedCategory.value = category
        loadClubs()
    }

    fun toggleFollow(clubId: String) {
        viewModelScope.launch {
            repository.toggleFollowClub(clubId)
            loadClubs()
            if (_selectedClubState.value?.data?.clubId == clubId) {
                loadClubDetail(clubId)
            }
        }
    }

    fun loadClubDetail(clubId: String) {
        _selectedClubState.value = Resource.Loading()
        _clubPostsState.value = Resource.Loading()
        _clubEventsState.value = Resource.Loading()
        viewModelScope.launch {
            _selectedClubState.value = repository.getClubById(clubId)
            _clubPostsState.value = repository.getPosts(clubId = clubId)
            _clubEventsState.value = repository.getEvents()
        }
    }
}
