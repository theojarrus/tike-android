package com.theost.tike.data.models.ui

import com.theost.tike.data.models.core.User
import com.theost.tike.ui.interfaces.DelegateItem

data class ProfileUi(
    val uid: String,
    val name: String,
    val nick: String,
    val avatar: String,
    val isBlocked: Boolean,
    val hasAccess: Boolean
) : DelegateItem {
    override fun id(): Any = uid
    override fun content(): Any = nick
}

fun User.mapToProfileUi(
    currentUid: String,
    currentUserBlocked: List<String> = emptyList(),
    profileUserBlocked: List<String> = emptyList()
): ProfileUi {
    return ProfileUi(
        uid = uid,
        name = name,
        nick = "@$nick",
        avatar = avatar,
        isBlocked = currentUserBlocked.contains(uid),
        hasAccess = !profileUserBlocked.contains(currentUid)
    )
}
