package com.theost.tike.feature.auth.presentation

import com.google.firebase.auth.AuthCredential
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.core.model.StateStatus.*
import com.theost.tike.core.presentation.BaseStateViewModel
import com.theost.tike.domain.model.multi.AuthStatus
import com.theost.tike.domain.model.multi.AuthStatus.SignedOut
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.CacheRepository
import com.theost.tike.feature.auth.business.DeleteAccountInteractor
import com.theost.tike.feature.auth.business.SignOutInteractor

class AuthViewModel : BaseStateViewModel<AuthState>() {

    private val signOutInteractor = SignOutInteractor(AuthRepository, CacheRepository)
    private val deleteAccountInteractor = DeleteAccountInteractor(AuthRepository, CacheRepository)

    fun updateAuthStatus(authStatus: AuthStatus) {
        update { copy(authStatus = authStatus) }
    }

    fun signOut() {
        update { copy(status = Loading) }
        disposableSwitch {
            signOutInteractor()
                .subscribe({
                    update { copy(status = Success, authStatus = SignedOut) }
                }, { error ->
                    update { copy(status = Error) }
                    log(this, error)
                })
        }
    }

    fun deleteAccount(credential: AuthCredential) {
        update { copy(status = Loading) }
        disposableSwitch {
            deleteAccountInteractor(credential)
                .subscribe({
                    update { copy(status = Success, authStatus = SignedOut) }
                }, { error ->
                    update { copy(status = Error) }
                    log(this, error)
                })
        }
    }
}
