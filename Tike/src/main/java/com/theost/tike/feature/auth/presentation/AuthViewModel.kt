package com.theost.tike.feature.auth.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.theost.tike.core.presentation.BaseRxViewModel
import com.theost.tike.domain.model.multi.AuthStatus

class AuthViewModel : BaseRxViewModel() {

    private val _authStatus = MutableLiveData<AuthStatus>()
    val authStatus: LiveData<AuthStatus> = _authStatus

    fun updateAuthStatus(authStatus: AuthStatus) {
        if (this.authStatus.value != authStatus) _authStatus.postValue(authStatus)
    }
}
