package com.theost.tike.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.tike.data.models.state.AuthStatus
import com.theost.tike.data.models.state.AuthStatus.SignedOut
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.state.Status.*
import com.theost.tike.data.repositories.AuthRepository
import com.theost.tike.ui.utils.LogUtils.LOG_VIEW_MODEL_AUTH
import io.reactivex.disposables.CompositeDisposable

class AuthViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _authStatus = MutableLiveData<AuthStatus>()
    val authStatus: LiveData<AuthStatus> = _authStatus

    private val compositeDisposable = CompositeDisposable()

    fun loadAuthStatus(initAuthStatus: AuthStatus? = null) {
        initAuthStatus?.let { setChangedAuthStatus(it) } ?: _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            AuthRepository.observeUserAuthStatus().subscribe({ status ->
                setChangedAuthStatus(status)
                _loadingStatus.postValue(Success)
            }, { error ->
                setChangedAuthStatus(SignedOut)
                _loadingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_AUTH, error.toString())
            })
        )
    }

    fun clearAuthData() {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            AuthRepository.signOut().subscribe({
                _loadingStatus.postValue(Success)
            }, { error ->
                _loadingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_AUTH, error.toString())
            })
        )
    }

    private fun setChangedAuthStatus(status: AuthStatus) {
        if (status != authStatus.value) _authStatus.postValue(status)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}