package com.theost.tike.feature.auth.presentation

import com.theost.tike.common.recycler.element.lifestyle.LifestyleUi
import com.theost.tike.core.model.StateStatus
import com.theost.tike.core.presentation.BaseState
import com.theost.tike.domain.model.core.User
import com.theost.tike.domain.model.multi.AuthStatus

data class SignUpState(
    override val status: StateStatus,
    val authStatus: AuthStatus,
    val user: User,
    val lifestyles: List<LifestyleUi>,
    val userLifestyles: List<String>,
    val isUserExist: Boolean,
    val isNameError: Boolean,
    val isNickError: Boolean
) : BaseState(status)
