package com.theost.tike.feature.schedule.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.threeten.bp.LocalDate

class CalendarViewModel : ViewModel() {

    private val _date = MutableLiveData<LocalDate?>()
    val date: LiveData<LocalDate?> = _date

    fun setDate(date: LocalDate?) = _date.postValue(date)
}
