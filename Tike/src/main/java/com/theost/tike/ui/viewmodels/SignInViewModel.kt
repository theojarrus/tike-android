package com.theost.tike.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.state.Status.*
import com.theost.tike.data.repositories.AuthRepository
import com.theost.tike.ui.utils.LogUtils.LOG_VIEW_MODEL_SIGN_IN
import io.reactivex.disposables.CompositeDisposable

class SignInViewModel : ViewModel() {

    private val _signingStatus = MutableLiveData<Status>()
    val signingStatus: LiveData<Status> = _signingStatus

    private val compositeDisposable = CompositeDisposable()

    fun signIn(credential: AuthCredential) {
        _signingStatus.postValue(Loading)
        compositeDisposable.add(
            AuthRepository.signIn(credential).subscribe({
                _signingStatus.postValue(Success)
            }, { error ->
                _signingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_SIGN_IN, error.toString())
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
