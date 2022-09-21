package com.theost.tike.feature.creation.joining.main.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.common.extension.hideNullableItem
import com.theost.tike.common.extension.mergeWith
import com.theost.tike.common.util.LogUtils.LOG_VIEW_MODEL_JOINING
import com.theost.tike.core.recycler.event.EventUi
import com.theost.tike.core.recycler.event.mapToEventUi
import com.theost.tike.core.recycler.user.UserUi
import com.theost.tike.core.recycler.user.mapToUserUi
import com.theost.tike.domain.model.multi.EventMode.JOINING
import com.theost.tike.domain.model.multi.Status
import com.theost.tike.domain.model.multi.Status.*
import com.theost.tike.domain.repository.EventsRepository
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class JoiningViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _creator = MutableLiveData<UserUi?>()
    val creator: LiveData<UserUi?> = _creator

    private val _events = MutableLiveData<List<EventUi>?>()
    val events: LiveData<List<EventUi>?> = _events

    private var creatorId: String? = null
    private var isListenerAttached = false

    private val compositeDisposable = CompositeDisposable()

    init {
        clearCreator()
    }

    fun init(uid: String?) {
        if (!isListenerAttached || uid != creatorId) {
            uid?.let { loadCreator(it) }
        }
    }

    private fun loadCreator(uid: String) {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapObservable { firebaseUser ->
                UsersRepository.observeUser(uid).map { it.mapToUserUi(firebaseUser.uid) }
            }.subscribe({ user ->
                creatorId = user.uid
                if (user.hasAccess) loadEvents(uid)
                _creator.postValue(user)
                _loadingStatus.postValue(Success)
            }, { error ->
                _loadingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_JOINING, error.toString())
            })
        )
    }

    private fun loadEvents(uid: String) {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapObservable { firebaseUser ->
                EventsRepository.observeProperVacantEvents(firebaseUser.uid, uid)
                    .switchMapSingle { events ->
                        Observable.fromIterable(events).concatMapSingle { event ->
                            UsersRepository.getUsers(event.pending.mergeWith(event.participants))
                                .map { users ->
                                    event.mapToEventUi(
                                        users.filter { it.uid != uid }
                                            .map { it.mapToUserUi(firebaseUser.uid) },
                                        JOINING
                                    )
                                }
                        }.toList()
                    }
            }.subscribe({ events ->
                isListenerAttached = true
                _events.postValue(events)
                _loadingStatus.postValue(Success)
            }, { error ->
                isListenerAttached = false
                _loadingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_JOINING, error.toString())
            })
        )
    }

    fun addEventRequest(id: String, creator: String) {
        _events.hideNullableItem(EventUi::id, id)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                EventsRepository.getEvent(creator, id).flatMapCompletable { event ->
                    EventsRepository.addReferenceRequestingEvent(
                        firebaseUser.uid,
                        id,
                        creator,
                        event.requesting,
                        event.date.dayOfWeek.value,
                        event.date.dayOfMonth,
                        event.date.monthValue,
                        event.date.year,
                        event.repeatMode
                    )
                }
            }.subscribe({
                Log.i(LOG_VIEW_MODEL_JOINING, "Event $id from $creator accepted")
            }, { error ->
                Log.e(LOG_VIEW_MODEL_JOINING, error.toString())
            })
        )
    }

    fun clearCreator() {
        compositeDisposable.clear()
        isListenerAttached = false
        _creator.postValue(null)
        _events.postValue(null)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
