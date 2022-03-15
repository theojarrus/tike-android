package com.theost.tike.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import org.threeten.bp.LocalDate

class ScheduleViewModel : ViewModel() {

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition

    private val _currentDay = MutableLiveData<CalendarDay>()
    val currentDay: LiveData<CalendarDay> = _currentDay

    private val _currentDate = MutableLiveData<LocalDate>()
    val currentDate: LiveData<LocalDate> = _currentDate

    init {
        _currentDate.postValue(LocalDate.now())
        setToday()
    }

    fun updateCurrentDay(day: CalendarDay, position: Int) {
        _currentDay.postValue(day)
        _currentPosition.postValue(position)
    }

    fun setToday() {
        _currentDay.postValue(CalendarDay.today())
        _currentPosition.postValue(0)
    }

}