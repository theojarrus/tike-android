package com.theost.tike.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.tike.data.models.ui.ListEvent
import com.theost.tike.data.models.state.Status
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.LocalDate

class ScheduleViewModel : ViewModel() {

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition

    fun updateCurrentPosition(position: Int) {
        _currentPosition.postValue(position)
    }

}