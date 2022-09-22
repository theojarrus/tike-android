package com.theost.tike.core.recycler.user

import com.theost.tike.domain.model.core.User

class UserToUserUiMapper : (User, String) -> UserUi {

    override fun invoke(user: User, auid: String): UserUi = with(user) {
        UserUi(
            uid = uid,
            name = name,
            nick = "@$nick",
            avatar = avatar,
            hasAccess = !blocked.contains(auid)
        )
    }
}
