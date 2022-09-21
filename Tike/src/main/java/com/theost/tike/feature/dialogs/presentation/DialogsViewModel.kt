package com.theost.tike.feature.dialogs.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.common.util.LogUtils
import com.theost.tike.core.recycler.user.UserUi
import com.theost.tike.core.recycler.user.mapToUserUi
import com.theost.tike.domain.model.multi.Status
import com.theost.tike.domain.model.multi.Status.*
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.disposables.CompositeDisposable

class DialogsViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _users = MutableLiveData<List<UserUi>>()
    val users: LiveData<List<UserUi>> = _users

    private var isDialogsLoaded = false

    private val compositeDisposable = CompositeDisposable()

    init {
        if (!isDialogsLoaded) loadUsers()
    }

    private fun loadUsers() {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapSingle { firebaseUser ->
                UsersRepository.getUser(firebaseUser.uid).flatMap { databaseUser ->
                    UsersRepository.getUsers(databaseUser.friends).map { users ->
                        users.map { it.mapToUserUi(firebaseUser.uid) }
                            .map { it.copy(nick = "Вы: чат создан") }
                    }
                }
            }.subscribe({ users ->
                isDialogsLoaded = true
                _users.postValue(users)
                _loadingStatus.postValue(Success)
            }, { error ->
                isDialogsLoaded = false
                _loadingStatus.postValue(Error)
                Log.e(LogUtils.LOG_VIEW_MODEL_FRIENDS, error.toString())
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
