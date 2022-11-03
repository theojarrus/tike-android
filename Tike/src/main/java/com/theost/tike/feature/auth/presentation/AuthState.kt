package com.theost.tike.feature.auth.presentation

import com.theost.tike.core.model.StateStatus
import com.theost.tike.core.presentation.BaseState
import com.theost.tike.domain.model.multi.AuthStatus

data class AuthState(
    override val status: StateStatus,
    val authStatus: AuthStatus
) : BaseState(status)
