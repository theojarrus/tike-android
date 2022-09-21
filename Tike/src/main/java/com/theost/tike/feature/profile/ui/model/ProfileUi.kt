package com.theost.tike.feature.profile.ui.model

import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.domain.model.core.User
import com.theost.tike.domain.model.multi.FriendStatus
import com.theost.tike.domain.model.multi.FriendStatus.*

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
