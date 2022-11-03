package com.theost.tike.feature.day.presentation

import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.core.model.StateStatus
import com.theost.tike.core.presentation.BaseState

data class DayState(
    override val status: StateStatus,
    val items: List<DelegateItem>
) : BaseState(status)
