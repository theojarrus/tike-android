package com.theost.tike.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.state.Status.*
import com.theost.tike.data.models.ui.UserUi
import com.theost.tike.data.models.ui.mapToUserUi
import com.theost.tike.data.repositories.EventsRepository
import com.theost.tike.data.repositories.UsersRepository
import com.theost.tike.ui.utils.LogUtils.LOG_VIEW_MODEL_FRIENDS
import io.reactivex.disposables.CompositeDisposable

class InfoViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _users = MutableLiveData<List<UserUi>>()
    val users: LiveData<List<UserUi>> = _users

    private val compositeDisposable = CompositeDisposable()

    fun init(id: String) {
        if (users.value == null) loadUsers(id)
    }

    private fun loadUsers(eventId: String) {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapSingle { firebaseUser ->
                EventsRepository.getEvent(firebaseUser.uid, eventId).flatMap { event ->
                    UsersRepository.getUsers(event.participants).map { users ->
                        users.filter { it.uid != firebaseUser.uid }
                            .map { it.mapToUserUi(firebaseUser.uid) }
                    }
                }
            }.subscribe({ users ->
                _users.postValue(users)
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
