package com.theost.tike.common.recycler.element.user

import com.theost.tike.domain.model.core.User

class UserUiMapper : (User, String) -> UserUi {

    override fun invoke(user: User, auid: String): UserUi = with(user) {
        UserUi(
            uid = uid,
            name = name.takeIf { isActive },
            nick = "@$nick".takeIf { isActive },
            avatar = avatar.takeIf { isActive },
            hasAccess = !blocked.contains(auid),
        )
    }
}
