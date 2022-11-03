package com.theost.tike.feature.splash.presentation

import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.core.model.StateStatus.*
import com.theost.tike.core.presentation.BaseStateViewModel
import com.theost.tike.domain.model.multi.AuthStatus.SignedOut
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.feature.splash.business.GetAuthStatusInteractor

class SplashViewModel : BaseStateViewModel<SplashState>() {

    private val getAuthStatusInteractor = GetAuthStatusInteractor(AuthRepository)

    fun fetchAuth() {
        update { copy(status = Loading) }
        disposableSwitch {
            getAuthStatusInteractor()
                .subscribe({ authStatus ->
                    update { copy(status = Success, authStatus = authStatus) }
                }, { error ->
                    update { copy(status = Error, authStatus = SignedOut) }
                    log(this, error)
                })
        }
    }
}
