package com.theost.tike.data.models.ui

import com.theost.tike.data.models.core.User
import com.theost.tike.data.models.state.FriendMode
import com.theost.tike.ui.interfaces.DelegateItem

data class FriendUi(
    val uid: String,
    val name: String,
    val nick: String,
    val avatar: String,
    val hasAccess: Boolean,
    val mode: FriendMode
) : DelegateItem {
    override fun id(): Any = uid
    override fun content(): Any = name + nick + avatar + hasAccess + mode
}

fun User.mapToFriendUi(currentUid: String = "", mode: FriendMode): FriendUi {
    return FriendUi(
        uid = uid,
        name = name,
        nick = "@$nick",
        avatar = avatar,
        hasAccess = !blocked.contains(currentUid),
        mode = mode
    )
}
