package com.theost.tike.common.recycler.element.member

import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.domain.model.core.User

data class MemberUi(
    val uid: String,
    val name: String?,
    val nick: String?,
    val avatar: String?,
    val hasAccess: Boolean,
    val isSelected: Boolean
) : DelegateItem {
    override fun id(): String = uid
    override fun content(): String = name + nick + avatar + hasAccess + isSelected
}

fun User.mapToParticipantUi(currentUid: String): MemberUi {
    return MemberUi(
        uid = uid,
        name = name.takeIf { isActive },
        nick = "@$nick".takeIf { isActive },
        avatar = avatar.takeIf { isActive },
        hasAccess = !blocked.contains(currentUid),
        isSelected = false,
    )
}
