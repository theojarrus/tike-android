package com.theost.tike.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.tike.data.models.core.Location

class EventViewModel : ViewModel() {

    private val _members = MutableLiveData<List<String>>()
    val members: LiveData<List<String>> = _members

    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    init {
        _members.value = emptyList()
    }

    fun reset() {
        _members.value = emptyList()
        _location.value = null
    }

    fun setMembers(members: List<String>) {
        _members.value = members
    }

    fun setLocation(location: Location?) {
        _location.value = location
    }
}