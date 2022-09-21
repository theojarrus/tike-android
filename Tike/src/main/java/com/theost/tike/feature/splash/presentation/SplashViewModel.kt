package com.theost.tike.feature.splash.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.tike.common.util.LogUtils.LOG_VIEW_MODEL_SPLASH
import com.theost.tike.domain.model.multi.AuthStatus
import com.theost.tike.domain.model.multi.AuthStatus.SignedOut
import com.theost.tike.domain.repository.AuthRepository
import io.reactivex.disposables.CompositeDisposable

class SplashViewModel : ViewModel() {

    private val _authStatus = MutableLiveData<AuthStatus>()
    val authStatus: LiveData<AuthStatus> = _authStatus

    private val compositeDisposable = CompositeDisposable()

    fun init() {
        compositeDisposable.add(AuthRepository.getUserAuthStatus().subscribe({ status ->
            _authStatus.postValue(status)
        }, { error ->
            _authStatus.postValue(SignedOut)
            Log.e(LOG_VIEW_MODEL_SPLASH, error.toString())
        }))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
