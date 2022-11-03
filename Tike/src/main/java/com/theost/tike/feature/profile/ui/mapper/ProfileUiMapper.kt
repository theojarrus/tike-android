package com.theost.tike.feature.profile.ui.mapper

import com.theost.tike.domain.model.core.User
import com.theost.tike.domain.model.multi.FriendStatus
import com.theost.tike.domain.model.multi.FriendStatus.*
import com.theost.tike.feature.profile.ui.model.ProfileUi

class ProfileUiMapper : (User, User) -> ProfileUi {

    override fun invoke(user: User, actual: User): ProfileUi = with(user) {
        ProfileUi(
            uid = uid,
            name = name.takeIf { isActive },
            nick = "@$nick".takeIf { isActive },
            avatar = avatar.takeIf { isActive },
            isActive = isActive,
            hasAccess = !blocked.contains(actual.uid),
            isBlocked = actual.blocked.contains(uid),
            isActual = user.uid == actual.uid,
            friendStatus = getFriendStatus(uid, actual)
        )
    }

    private fun getFriendStatus(uid: String, actual: User): FriendStatus = when {
        actual.friends.contains(uid) -> FRIEND
        actual.pending.contains(uid) -> PENDING
        actual.requesting.contains(uid) -> REQUESTING
        else -> NOT_FRIEND
    }
}
