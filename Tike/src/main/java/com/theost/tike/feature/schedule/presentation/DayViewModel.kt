package com.theost.tike.feature.schedule.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.common.extension.hideItem
import com.theost.tike.common.extension.mergeWith
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.common.recycler.element.event.EventUi
import com.theost.tike.common.recycler.element.event.mapToEventUi
import com.theost.tike.common.recycler.element.user.mapToUserUi
import com.theost.tike.common.util.LogUtils.LOG_VIEW_MODEL_DAY
import com.theost.tike.domain.model.multi.EventMode.SCHEDULE_PROPER
import com.theost.tike.domain.model.multi.EventMode.SCHEDULE_REFERENCE
import com.theost.tike.domain.model.multi.EventType.REFERENCE
import com.theost.tike.domain.model.multi.Status
import com.theost.tike.domain.model.multi.Status.*
import com.theost.tike.domain.repository.EventsRepository
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.LocalDate

class DayViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _events = MutableLiveData<List<DelegateItem>>()
    val events: LiveData<List<DelegateItem>> = _events

    private var isListenerAttached = false
    private val compositeDisposable = CompositeDisposable()

    fun init(date: LocalDate) {
        if (!isListenerAttached) loadEvents(date)
    }

    private fun loadEvents(date: LocalDate) {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapObservable { firebaseUser ->
                EventsRepository.observeEvents(
                    firebaseUser.uid,
                    date.year,
                    date.monthValue,
                    date.dayOfMonth,
                    date.dayOfWeek.value
                ).switchMapSingle { events ->
                    Observable.fromIterable(events).concatMapSingle { event ->
                        UsersRepository.getUsers(event.participants).map { users ->
                            event.mapToEventUi(
                                users.filter { it.uid != firebaseUser.uid }
                                    .map { it.mapToUserUi(firebaseUser.uid) },
                                if (event.type == REFERENCE) SCHEDULE_REFERENCE else SCHEDULE_PROPER
                            )
                        }
                    }.toList()
                }
            }.subscribe({
                isListenerAttached = true
                _events.postValue(it)
                _loadingStatus.postValue(Success)
            }, { error ->
                isListenerAttached = false
                _events.postValue(emptyList())
                _loadingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_DAY, error.toString())
            })
        )
    }

    fun deleteProperEvent(id: String) {
        _events.hideItem(EventUi::id, id)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                EventsRepository.getEvent(firebaseUser.uid, id).flatMapCompletable { event ->
                    EventsRepository.deleteProperEvent(
                        firebaseUser.uid,
                        id,
                        event.participants.mergeWith(event.requesting, event.pending)
                    )
                }
            }.subscribe({
                Log.e(LOG_VIEW_MODEL_DAY, "Proper Event $id successfully deleted")
            }, { error ->
                Log.e(LOG_VIEW_MODEL_DAY, error.toString())
            })
        )
    }

    fun deleteReferenceEvent(id: String, creator: String) {
        _events.hideItem(EventUi::id, id)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                EventsRepository.getEvent(creator, id).flatMapCompletable { event ->
                    EventsRepository.deleteReferenceEvent(
                        firebaseUser.uid,
                        id,
                        creator,
                        event.participantsLimit
                    )
                }
            }.subscribe({
                Log.e(LOG_VIEW_MODEL_DAY, "Reference Event $id successfully deleted")
            }, { error ->
                Log.e(LOG_VIEW_MODEL_DAY, error.toString())
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
