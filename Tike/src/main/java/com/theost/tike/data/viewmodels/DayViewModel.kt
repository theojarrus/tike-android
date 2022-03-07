package com.theost.tike.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.tike.data.models.core.Event
import com.theost.tike.data.models.ui.ListEvent
import com.theost.tike.data.models.ui.mapToListEvent
import com.theost.tike.data.repositories.EventsRepository
import com.theost.tike.data.state.Status
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.LocalDate

class DayViewModel : ViewModel() {

    private val _allData = MutableLiveData<List<ListEvent>>()
    val allData: LiveData<List<ListEvent>> = _allData

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val compositeDisposable = CompositeDisposable()

    fun loadData(date: LocalDate) {
        compositeDisposable.add(
            EventsRepository.getEvents(date.toEpochDay())
                .map { events -> events.map { event -> event.mapToListEvent() } }
                .subscribe({ events ->
                    _allData.postValue(events)
                }, { error ->
                    error.printStackTrace()
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}