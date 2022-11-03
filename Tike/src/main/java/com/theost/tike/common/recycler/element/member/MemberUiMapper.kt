package com.theost.tike.common.recycler.element.member

import com.theost.tike.domain.model.core.User

class MemberUiMapper : (User, String) -> MemberUi {

    override fun invoke(user: User, auid: String): MemberUi = with(user) {
        MemberUi(
            uid = uid,
            name = name,
            nick = "@$nick",
            avatar = avatar.takeIf { isActive },
            hasAccess = !blocked.contains(auid),
            isSelected = false,
        )
    }
}
