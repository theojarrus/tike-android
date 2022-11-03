package com.theost.tike.feature.profile.ui.model

import com.theost.tike.domain.model.multi.FriendStatus

data class ProfileUi(
    val uid: String,
    val name: String?,
    val nick: String?,
    val avatar: String?,
    val isActive: Boolean,
    val hasAccess: Boolean,
    val isBlocked: Boolean,
    val isActual: Boolean,
    val friendStatus: FriendStatus
)
