package com.campusconnect.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusconnect.app.data.model.Event
import com.campusconnect.app.data.model.Registration
import com.campusconnect.app.data.repository.CampusRepository
import com.campusconnect.app.utils.Resource
import kotlinx.coroutines.launch

class EventViewModel(private val repository: CampusRepository) : ViewModel() {
    private val _eventsState = MutableLiveData<Resource<List<Event>>>()
    val eventsState: LiveData<Resource<List<Event>>> = _eventsState

    private val _selectedEventState = MutableLiveData<Resource<Event>>()
    val selectedEventState: LiveData<Resource<Event>> = _selectedEventState

    private val _registrationResult = MutableLiveData<Resource<Registration>?>()
    val registrationResult: LiveData<Resource<Registration>?> = _registrationResult

    private val _myRegistrationsState = MutableLiveData<Resource<List<Registration>>>()
    val myRegistrationsState: LiveData<Resource<List<Registration>>> = _myRegistrationsState

    init {
        loadEvents()
    }

    fun loadEvents(category: String? = null, timeFilter: String? = null) {
        _eventsState.value = Resource.Loading()
        viewModelScope.launch {
            _eventsState.value = repository.getEvents(category, timeFilter)
        }
    }

    fun loadEventDetail(eventId: String) {
        _selectedEventState.value = Resource.Loading()
        viewModelScope.launch {
            _selectedEventState.value = repository.getEventById(eventId)
        }
    }




    fun registerForEvent(eventId: String) {
        android.util.Log.e(
            "CampusConnect",
            "REGISTER BUTTON TRIGGERED: eventId=$eventId"
        )

        _registrationResult.value = Resource.Loading()

        viewModelScope.launch {
            try {
                android.util.Log.e(
                    "CampusConnect",
                    "CALLING REGISTRATION REPOSITORY"
                )

                val result = repository.registerForEvent(eventId)

                android.util.Log.e(
                    "CampusConnect",
                    "REPOSITORY RESULT: ${result::class.simpleName}"
                )

                if (result is Resource.Error) {
                    android.util.Log.e(
                        "CampusConnect",
                        "REGISTRATION ERROR: ${result.message}"
                    )
                }

                _registrationResult.value = result

                if (result is Resource.Success) {
                    android.util.Log.e(
                        "CampusConnect",
                        "REGISTRATION SUCCESSFUL"
                    )

                    loadEventDetail(eventId)
                    loadMyRegistrations()
                }
            } catch (e: Exception) {
                android.util.Log.e(
                    "CampusConnect",
                    "VIEWMODEL REGISTRATION EXCEPTION",
                    e
                )

                _registrationResult.value = Resource.Error(
                    e.message ?: "Registration failed."
                )
            }
        }
    }



    fun clearRegistrationResult() {
        _registrationResult.value = null
    }

    fun loadMyRegistrations() {
        _myRegistrationsState.value = Resource.Loading()
        viewModelScope.launch {
            _myRegistrationsState.value = repository.getMyRegistrations()
        }
    }
}
