package com.theost.tike.feature.auth.presentation

import com.theost.tike.core.component.model.StateStatus
import com.theost.tike.core.component.presentation.BaseState
import com.theost.tike.domain.model.multi.AuthStatus

data class SignInState(
    override val status: StateStatus,
    val authStatus: AuthStatus
) : BaseState(status)
