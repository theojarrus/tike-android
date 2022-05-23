package com.theost.tike.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.data.models.state.EventMode.SCHEDULE_PROPER
import com.theost.tike.data.models.state.EventMode.SCHEDULE_REFERENCE
import com.theost.tike.data.models.state.EventType.REFERENCE
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.state.Status.*
import com.theost.tike.data.models.ui.mapToEventUi
import com.theost.tike.data.models.ui.mapToUserUi
import com.theost.tike.data.repositories.EventsRepository
import com.theost.tike.data.repositories.UsersRepository
import com.theost.tike.ui.extensions.mergeWith
import com.theost.tike.ui.interfaces.DelegateItem
import com.theost.tike.ui.utils.LogUtils.LOG_VIEW_MODEL_DAY
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
