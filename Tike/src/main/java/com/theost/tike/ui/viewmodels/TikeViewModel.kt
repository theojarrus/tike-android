package com.theost.tike.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.data.models.state.AuthStatus
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.repositories.AuthRepository
import com.theost.tike.data.repositories.UsersRepository
import io.reactivex.disposables.CompositeDisposable

class TikeViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _authStatus = MutableLiveData<AuthStatus>()
    val authStatus: LiveData<AuthStatus> = _authStatus

    private val compositeDisposable = CompositeDisposable()

    init {
        _loadingStatus.postValue(Status.Loading)
        compositeDisposable.add(AuthRepository.getAuthStatus().subscribe({ status ->
            if (status != authStatus.value) _authStatus.postValue(status)
            _loadingStatus.postValue(Status.Success)
        }, { _loadingStatus.postValue(Status.Error) }))
    }

    fun deleteAccount() {
        _loadingStatus.postValue(Status.Loading)
        Firebase.auth.currentUser?.let { user ->
            UsersRepository.deleteUser(user.uid).subscribe({
                user.delete()
                signOut()
                _loadingStatus.postValue(Status.Success)
            }, {
                _loadingStatus.postValue(Status.Error)
            })
        }
    }

    fun signOut() {
        Firebase.auth.signOut()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}