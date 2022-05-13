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
import com.theost.tike.ui.utils.LogUtils.LOG_VIEW_MODEL_FRIENDS
import io.reactivex.disposables.CompositeDisposable

class FriendsViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _users = MutableLiveData<List<UserUi>>()
    val users: LiveData<List<UserUi>> = _users

    private var cachedUsers = emptyList<UserUi>()
    private val compositeDisposable = CompositeDisposable()

    init {
        loadUsers()
    }

    fun searchUsers(query: String) {
        _users.postValue(
            cachedUsers.filter {
                it.name.lowercase().contains(query).or(it.nick.lowercase().contains(query))
            }
        )
    }

    private fun loadUsers() {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).toSingle().flatMap { firebaseUser ->
                UsersRepository.getUser(firebaseUser.uid).flatMap { databaseUser ->
                    UsersRepository.getUsers(databaseUser.friends)
                        .map { users -> users.map { user -> user.mapToUserUi(firebaseUser.uid) } }
                }
            }.subscribe({ users ->
                cachedUsers = users
                _users.postValue(users)
                _loadingStatus.postValue(Success)
            }, { error ->
                _loadingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_FRIENDS, error.toString())
            })
        )
    }
}
