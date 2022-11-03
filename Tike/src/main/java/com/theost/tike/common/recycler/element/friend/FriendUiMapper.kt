package com.theost.tike.common.recycler.element.friend

import com.theost.tike.domain.model.core.User
import com.theost.tike.domain.model.multi.Direction

class FriendUiMapper : (User, String, Direction) -> FriendUi {

    override fun invoke(
        user: User,
        auid: String,
        direction: Direction
    ): FriendUi = with(user) {
        FriendUi(
            uid = uid,
            name = name.takeIf { isActive },
            nick = "@$nick".takeIf { isActive },
            avatar = avatar.takeIf { isActive },
            isActive = isActive,
            hasAccess = !blocked.contains(auid),
            direction = direction
        )
    }
}
