package com.theost.tike.feature.blacklist.presentation

import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.core.model.StateStatus
import com.theost.tike.core.presentation.BaseState

data class BlacklistState(
    override val status: StateStatus,
    val items: List<DelegateItem>,
    val cache: List<DelegateItem>,
    val query: String?
) : BaseState(status)
