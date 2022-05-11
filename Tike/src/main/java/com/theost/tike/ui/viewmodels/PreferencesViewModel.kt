package com.theost.tike.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.theost.tike.data.models.state.AuthStatus
import com.theost.tike.data.models.state.AuthStatus.SignedOut
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.state.Status.*
import com.theost.tike.data.repositories.AuthRepository
import com.theost.tike.ui.utils.LogUtils.LOG_VIEW_MODEL_PREFERENCES
import io.reactivex.disposables.CompositeDisposable

class PreferencesViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _authStatus = MutableLiveData<AuthStatus>()
    val authStatus: LiveData<AuthStatus> = _authStatus

    private val compositeDisposable = CompositeDisposable()

    fun delete(credential: AuthCredential) {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            AuthRepository.delete(credential).subscribe({
                _authStatus.postValue(SignedOut)
                _loadingStatus.postValue(Success)
            }, { error ->
                _loadingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_PREFERENCES, error.toString())
            })
        )
    }

    fun signOut() {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            AuthRepository.signOut().subscribe({
                _authStatus.postValue(SignedOut)
                _loadingStatus.postValue(Success)
            }, { error ->
                _loadingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_PREFERENCES, error.toString())
            })
        )
    }
}