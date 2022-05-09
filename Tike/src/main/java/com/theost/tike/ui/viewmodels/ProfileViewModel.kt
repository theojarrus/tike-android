package com.theost.tike.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.state.Status.*
import com.theost.tike.data.models.ui.UserUi
import com.theost.tike.data.models.ui.mapToUserUi
import com.theost.tike.data.repositories.AuthRepository
import com.theost.tike.data.repositories.UsersRepository
import com.theost.tike.ui.utils.LogUtils.LOG_VIEW_MODEL_PROFILE
import io.reactivex.disposables.CompositeDisposable

class ProfileViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _changingStatus = MutableLiveData<Status>()
    val changingStatus: LiveData<Status> = _changingStatus

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
            }, { error ->
                _loadingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }

    private fun loadOtherUser(uid: String) {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            UsersRepository.getUser(uid).subscribe({ user ->
                _user.postValue(user.mapToUserUi())
                _loadingStatus.postValue(Success)
            }, { error ->
                _loadingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }

    fun delete(credential: AuthCredential) {
        _changingStatus.postValue(Loading)
        compositeDisposable.add(
            AuthRepository.delete(credential).subscribe({
                _changingStatus.postValue(Success)
            }, { error ->
                _changingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }

    fun signOut() {
        _changingStatus.postValue(Loading)
        compositeDisposable.add(
            AuthRepository.signOut().subscribe({
                _changingStatus.postValue(Success)
            }, { error ->
                _changingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }
}