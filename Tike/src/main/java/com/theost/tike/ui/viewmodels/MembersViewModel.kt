package com.theost.tike.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MembersViewModel : ViewModel() {

    private val _members = MutableLiveData<List<String>>()
    val members: LiveData<List<String>> = _members

    init {
        _members.value = emptyList()
    }

    fun setMembers(members: List<String>) = _members.postValue(members)
}