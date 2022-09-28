package com.theost.tike.feature.auth.presentation

import com.google.firebase.auth.AuthCredential
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.core.model.StateStatus.*
import com.theost.tike.core.presentation.BaseStateViewModel
import com.theost.tike.domain.model.multi.AuthStatus.SignedOut
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.feature.auth.business.FetchSignIn

class SignInViewModel : BaseStateViewModel<SignInState>() {

    fun signIn(credential: AuthCredential) {
        update { copy(status = Loading) }
        disposable {
            FetchSignIn(AuthRepository).invoke(credential)
                .subscribe({ authStatus ->
                    update { copy(status = Success, authStatus = authStatus) }
                }, { error ->
                    log(this, error)
                    update { copy(status = Error, authStatus = SignedOut) }
                })
        }
    }
}
