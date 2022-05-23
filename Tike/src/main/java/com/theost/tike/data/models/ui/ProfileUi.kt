package com.theost.tike.data.models.ui

import com.theost.tike.data.models.core.User
import com.theost.tike.data.models.state.FriendStatus
import com.theost.tike.data.models.state.FriendStatus.*
import com.theost.tike.ui.interfaces.DelegateItem

data class ProfileUi(
    val uid: String,
    val name: String,
    val nick: String,
    val avatar: String,
    val isBlocked: Boolean,
    val hasAccess: Boolean,
    val friendStatus: FriendStatus
) : DelegateItem {
    override fun id(): Any = uid
    override fun content(): Any = nick
}

fun User.mapToProfileUi(current: User): ProfileUi {
    return ProfileUi(
        uid = uid,
        name = name,
        nick = "@$nick",
        avatar = avatar,
        isBlocked = current.blocked.contains(uid),
        hasAccess = !blocked.contains(current.uid),
        friendStatus = when {
            current.friends.contains(uid) -> FRIEND
            current.pending.contains(uid) -> PENDING
            pending.contains(current.uid) -> REQUESTING
            else -> NOT_FRIEND
        }
    )
}
