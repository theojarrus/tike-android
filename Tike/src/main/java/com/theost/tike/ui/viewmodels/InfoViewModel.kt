package com.theost.tike.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.R
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.state.Status.*
import com.theost.tike.data.models.ui.TitleUi
import com.theost.tike.data.models.ui.mapToUserUi
import com.theost.tike.data.repositories.EventsRepository
import com.theost.tike.data.repositories.UsersRepository
import com.theost.tike.ui.interfaces.DelegateItem
import com.theost.tike.ui.utils.LogUtils.LOG_VIEW_MODEL_FRIENDS
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

class InfoViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _items = MutableLiveData<List<DelegateItem>>()
    val items: LiveData<List<DelegateItem>> = _items

    private val compositeDisposable = CompositeDisposable()

    fun init(id: String, creator: String) {
        if (items.value == null) loadInfo(id, creator)
    }

    private fun loadInfo(id: String, creator: String) {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapSingle { firebaseUser ->
                EventsRepository.getEvent(creator, id).flatMap { event ->
                    Single.zip(
                        UsersRepository.getUser(event.creatorId).map { user ->
                            user.mapToUserUi(firebaseUser.uid)
                        },
                        UsersRepository.getUsers(event.participants).map { users ->
                            users.filter { it.uid != firebaseUser.uid && it.uid != creator }
                                .map { it.mapToUserUi(firebaseUser.uid) }
                        },
                        UsersRepository.getUsers(event.pending).map { users ->
                            users.filter { it.uid != firebaseUser.uid && it.uid != creator }
                                .map { it.mapToUserUi(firebaseUser.uid) }
                        },
                        UsersRepository.getUsers(event.requesting).map { users ->
                            users.filter { it.uid != firebaseUser.uid && it.uid != creator }
                                .map { it.mapToUserUi(firebaseUser.uid) }
                        }
                    ) { eventCreator, eventParticipants, eventPending, eventRequesting ->
                        mutableListOf<DelegateItem>().apply {
                            if (eventCreator.uid != firebaseUser.uid) {
                                add(TitleUi(R.string.creator))
                                add(eventCreator)
                            }
                            if (eventParticipants.isNotEmpty()) {
                                add(TitleUi(R.string.event_participants))
                                addAll(eventParticipants)
                            }
                            if (eventPending.isNotEmpty()) {
                                add(TitleUi(R.string.event_pending))
                                addAll(eventPending)
                            }
                            if (eventRequesting.isNotEmpty()) {
                                add(TitleUi(R.string.event_requesting))
                                addAll(eventRequesting)
                            }
                        }
                    }
                }
            }.subscribe({ items ->
                _items.postValue(items)
                _loadingStatus.postValue(Success)
            }, { error ->
                _loadingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_FRIENDS, error.toString())
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
