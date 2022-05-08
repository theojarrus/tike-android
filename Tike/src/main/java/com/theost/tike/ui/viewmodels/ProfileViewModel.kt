package com.theost.tike.ui.viewmodels

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
import io.reactivex.disposables.CompositeDisposable

class ProfileViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _user = MutableLiveData<UserUi>()
    val user: LiveData<UserUi> = _user

    private val compositeDisposable = CompositeDisposable()

    fun init(uid: String? = null) {
        if (user.value == null) loadUser(uid)
    }

    private fun loadUser(uid: String? = null) {
        when (uid.isNullOrEmpty()) {
            true -> loadCurrentUser()
            false -> loadOtherUser(uid)
        }
    }

    private fun loadCurrentUser() {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).toSingle().flatMap { firebaseUser ->
                UsersRepository.getUser(firebaseUser.uid)
            }.subscribe({ user ->
                _user.postValue(user.mapToUserUi())
                _loadingStatus.postValue(Success)
            }, {
                _loadingStatus.postValue(Error)
            })
        )
    }

    private fun loadOtherUser(uid: String) {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            UsersRepository.getUser(uid).subscribe({ user ->
                _user.postValue(user.mapToUserUi())
                _loadingStatus.postValue(Success)
            }, {
                _loadingStatus.postValue(Error)
            })
        )
    }
}