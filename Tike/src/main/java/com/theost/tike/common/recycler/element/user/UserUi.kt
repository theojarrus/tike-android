package com.theost.tike.common.recycler.element.user

import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.domain.model.core.User

data class UserUi(
    val uid: String,
    val name: String?,
    val nick: String?,
    val avatar: String?,
    val hasAccess: Boolean
) : DelegateItem {

    override fun id(): String = uid
    override fun content(): String = name + nick + avatar + hasAccess

    fun isRespondQuery(query: String?): Boolean {
        return query == null || nick.orEmpty().lowercase().contains(query)
                || name.orEmpty().lowercase().contains(query)
    }
}

fun User.mapToUserUi(currentUid: String = ""): UserUi {
    return UserUi(
        uid = uid,
        name = name.takeIf { isActive },
        nick = "@$nick".takeIf { isActive },
        avatar = avatar.takeIf { isActive },
        hasAccess = !blocked.contains(currentUid),
    )
}
