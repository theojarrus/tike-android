package com.theost.tike.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MemberViewModel : ViewModel() {

    private val _creator = MutableLiveData<String?>()
    val creator: LiveData<String?> = _creator

    fun setCreator(creator: String?) = _creator.postValue(creator)
}
