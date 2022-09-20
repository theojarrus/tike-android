package com.theost.tike.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.tike.data.models.core.Location

class LocationViewModel : ViewModel() {

    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    fun init(initialLocation: Location?) {
        location.value ?: setLocation(initialLocation)
    }

    fun setLocation(location: Location?) {
        _location.value = location
    }
}