package com.theost.tike.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.tike.data.models.ui.mapToListEvent
import com.theost.tike.data.repositories.EventsRepository
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.ui.mapToListParticipant
import com.theost.tike.data.repositories.UsersRepository
import com.theost.tike.ui.interfaces.DelegateItem
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.LocalDate

class DayViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _allData = MutableLiveData<List<DelegateItem>>()
    val allData: LiveData<List<DelegateItem>> = _allData

    private val compositeDisposable = CompositeDisposable()
    private var isListenerAttached = false

    fun loadEvents(date: LocalDate) {
        if (!isListenerAttached) {
            _loadingStatus.postValue(Status.Loading)
            compositeDisposable.add(
                EventsRepository.getEvents(date.year, date.monthValue, date.dayOfMonth)
                    .switchMapSingle { events ->
                        Observable.fromIterable(events).concatMapSingle { event ->
                            if (event.participants.isNotEmpty()) {
                                UsersRepository.getUsers(event.participants).map { users ->
                                    event.mapToListEvent(users.map { user -> user.mapToListParticipant() })
                                }
                            } else {
                                Single.just(event.mapToListEvent(emptyList()))
                            }
                        }.toList()
                    }
                    .subscribe({ events ->
                        isListenerAttached = true
                        _allData.postValue(events)
                        _loadingStatus.postValue(Status.Success)
                    }, { error ->
                        _loadingStatus.postValue(Status.Error(error))
                        error.printStackTrace()
                    })
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}