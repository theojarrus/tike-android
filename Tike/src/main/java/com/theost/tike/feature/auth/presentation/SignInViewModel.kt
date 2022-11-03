package com.theost.tike.feature.auth.presentation

import com.google.firebase.auth.AuthCredential
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.core.model.StateStatus.*
import com.theost.tike.core.presentation.BaseStateViewModel
import com.theost.tike.domain.model.multi.AuthStatus.SignedOut
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.feature.auth.business.FetchSignInInteractor

class SignInViewModel : BaseStateViewModel<SignInState>() {

    private val fetchSignInInteractor = FetchSignInInteractor(AuthRepository)

    fun signIn(credential: AuthCredential) {
        update { copy(status = Loading) }
        disposable {
            fetchSignInInteractor(credential)
                .subscribe({ authStatus ->
                    update { copy(status = Success, authStatus = authStatus) }
                }, { error ->
                    update { copy(status = Error, authStatus = SignedOut) }
                    log(this, error)
                })
        }
    }
}
