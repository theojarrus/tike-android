package com.theost.tike.core.recycler.friend

import com.theost.tike.domain.model.core.User
import com.theost.tike.domain.model.multi.FriendMode

class UserToFriendUiMapper : (User, String, FriendMode) -> FriendUi {

    override fun invoke(user: User, auid: String, mode: FriendMode): FriendUi = with(user) {
        FriendUi(
            uid = uid,
            name = name,
            nick = "@$nick",
            avatar = avatar,
            hasAccess = !blocked.contains(auid),
            mode = mode
        )
    }
}
