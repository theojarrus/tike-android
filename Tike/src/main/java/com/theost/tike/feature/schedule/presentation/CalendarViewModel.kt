package com.theost.tike.feature.schedule.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.threeten.bp.LocalDate

class CalendarViewModel : ViewModel() {

    private val _pendingDate = MutableLiveData<LocalDate?>()
    val pendingDate: LiveData<LocalDate?> = _pendingDate

    fun setPendingDate(date: LocalDate?) = _pendingDate.postValue(date)
}