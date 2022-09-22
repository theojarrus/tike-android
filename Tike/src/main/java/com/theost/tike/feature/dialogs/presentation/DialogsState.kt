package com.theost.tike.feature.dialogs.presentation

import com.theost.tike.core.component.model.StateStatus
import com.theost.tike.core.component.presentation.BaseState
import com.theost.tike.core.recycler.user.UserUi

data class DialogsState(
    override val status: StateStatus,
    val items: List<UserUi>
) : BaseState(status)
