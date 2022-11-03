package com.theost.tike.feature.creation.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddingViewModel : ViewModel() {

    private val _position = MutableLiveData<Int>()
    val position: LiveData<Int> = _position

    init {
        setPosition(0)
    }

    fun setPosition(position: Int) {
        _position.value = position
    }
}
