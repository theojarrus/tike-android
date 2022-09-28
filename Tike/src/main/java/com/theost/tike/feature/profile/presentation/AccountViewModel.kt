package com.theost.tike.feature.profile.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.common.recycler.element.user.UserUi
import com.theost.tike.common.recycler.element.user.mapToUserUi
import com.theost.tike.common.util.LogUtils.LOG_VIEW_MODEL_PROFILE
import com.theost.tike.domain.model.multi.Status
import com.theost.tike.domain.model.multi.Status.*
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.disposables.CompositeDisposable

class AccountViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _user = MutableLiveData<UserUi>()
    val user: LiveData<UserUi> = _user

    private val compositeDisposable = CompositeDisposable()

    init {
        loadUser()
    }

    private fun loadUser() {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapSingle { firebaseUser ->
                UsersRepository.getUser(firebaseUser.uid).map { it.mapToUserUi() }
            }.subscribe({ user ->
                _user.postValue(user)
                _loadingStatus.postValue(Success)
            }, { error ->
                _loadingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
