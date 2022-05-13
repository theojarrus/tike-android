package com.theost.tike.data.models.ui

import com.theost.tike.data.models.core.User
import com.theost.tike.ui.interfaces.DelegateItem

data class ParticipantUi(
    val uid: String,
    val name: String,
    val nick: String,
    val avatar: String,
    val hasAccess: Boolean,
    val isSelected: Boolean
) : DelegateItem {
    override fun id(): Any = uid
    override fun content(): Any = isSelected
}

fun User.mapToParticipantUi(currentUid: String): ParticipantUi {
    return ParticipantUi(
        uid = uid,
        name = name,
        nick = "@$nick",
        avatar = avatar,
        hasAccess = !blocked.contains(currentUid),
        isSelected = false
    )
}
