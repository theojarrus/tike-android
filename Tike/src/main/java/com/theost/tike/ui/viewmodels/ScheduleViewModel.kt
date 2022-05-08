package com.theost.tike.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.theost.tike.data.models.core.Dates
import com.theost.tike.data.repositories.EventsRepository
import com.theost.tike.ui.utils.LogUtils.LOG_VIEW_MODEL_DAY
import io.reactivex.disposables.CompositeDisposable

class ScheduleViewModel : ViewModel() {

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition

    private val _currentDay = MutableLiveData<CalendarDay>()
    val currentDay: LiveData<CalendarDay> = _currentDay

    private val _events = MutableLiveData<Dates>()
    val events: LiveData<Dates> = _events

    private var isListenerAttached = false
    private val compositeDisposable = CompositeDisposable()

    fun init(position: Int) {
        if (currentPosition.value == null) setToday(position)
        if (!isListenerAttached) loadEvents()
    }

    private fun loadEvents() {
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).toSingle()
                .flatMapObservable { EventsRepository.observeProperEventsDates(it.uid) }
                .subscribe({ dates ->
                    isListenerAttached = true
                    _events.postValue(dates)
                }, { error ->
                    isListenerAttached = false
                    Log.e(LOG_VIEW_MODEL_DAY, error.toString())
                })
        )
    }

    fun updateCurrentDay(day: CalendarDay, position: Int) {
        _currentDay.value = day
        _currentPosition.value = position
    }

    fun setToday(position: Int) {
        updateCurrentDay(CalendarDay.today(), position)
    }
}