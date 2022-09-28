package com.theost.tike.feature.splash.presentation

import com.theost.tike.core.model.StateStatus
import com.theost.tike.core.presentation.BaseState
import com.theost.tike.domain.model.multi.AuthStatus

data class SplashState(
    override val status: StateStatus,
    val authStatus: AuthStatus
) : BaseState(status)
