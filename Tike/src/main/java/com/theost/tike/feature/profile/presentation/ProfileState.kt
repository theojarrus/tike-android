package com.theost.tike.feature.profile.presentation

import com.theost.tike.core.model.StateStatus
import com.theost.tike.core.presentation.BaseState
import com.theost.tike.feature.profile.ui.model.ProfileUi

data class ProfileState(
    override val status: StateStatus,
    val profile: ProfileUi?
) : BaseState(status)
