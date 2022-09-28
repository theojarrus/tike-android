package com.theost.tike.feature.splash.presentation

import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.core.model.StateStatus.*
import com.theost.tike.core.presentation.BaseStateViewModel
import com.theost.tike.domain.model.multi.AuthStatus.SignedOut
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.feature.splash.business.GetAuthStatus

class SplashViewModel : BaseStateViewModel<SplashState>() {

    fun fetchAuth() {
        update { copy(status = Loading) }
        disposable {
            GetAuthStatus(AuthRepository).invoke()
                .subscribe({ authStatus ->
                    update { copy(status = Success, authStatus = authStatus) }
                }, { error ->
                    log(this, error)
                    update { copy(status = Error, authStatus = SignedOut) }
                })
        }
    }
}
