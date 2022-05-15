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
import com.theost.tike.data.repositories.UsersRepository
import com.theost.tike.ui.extensions.append
import com.theost.tike.ui.utils.LogUtils.LOG_VIEW_MODEL_FRIENDS
import io.reactivex.disposables.CompositeDisposable

class PeopleViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _users = MutableLiveData<List<UserUi>>()
    val users: LiveData<List<UserUi>> = _users

    private var cachedUsers = emptyList<UserUi>()
    private var cachedQuery = ""
    private var isListenerAttached = false

    private val compositeDisposable = CompositeDisposable()

    init {
        if (!isListenerAttached) loadUsers()
    }

    private fun restoreState(users: List<UserUi>) {
        cachedUsers = users
        if (cachedQuery.isNotEmpty()) {
            searchUsers(cachedQuery)
        } else {
            _users.postValue(users)
        }
    }

    fun searchUsers(query: String) {
        cachedQuery = query
        _users.postValue(
            cachedUsers.filter {
                it.name.lowercase().contains(query).or(it.nick.lowercase().contains(query))
            }
        )
    }

    private fun loadUsers() {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapObservable { firebaseUser ->
                UsersRepository.observeUser(firebaseUser.uid).switchMapSingle { databaseUser ->
                    UsersRepository.getAllUsers(
                        databaseUser.friends.append(databaseUser.blocked).append(firebaseUser.uid)
                    ).map { users -> users.map { it.mapToUserUi(firebaseUser.uid) } }
                }
            }.subscribe({ users ->
                isListenerAttached = true
                restoreState(users)
                _loadingStatus.postValue(Success)
            }, { error ->
                isListenerAttached = false
                _loadingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_FRIENDS, error.toString())
            })
        )
    }
}
