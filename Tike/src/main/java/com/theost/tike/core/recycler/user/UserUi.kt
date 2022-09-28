package com.theost.tike.core.recycler.user

import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.domain.model.core.User

data class UserUi(
    val uid: String,
    val name: String,
    val nick: String,
    val avatar: String,
    val hasAccess: Boolean
) : DelegateItem {

    override fun id(): Any = uid
    override fun content(): Any = name + nick + avatar + hasAccess

    fun isRespondQuery(query: String): Boolean {
        return nick.lowercase().contains(query) || name.lowercase().contains(query)
    }
}

fun User.mapToUserUi(currentUid: String = ""): UserUi {
    return UserUi(
        uid = uid,
        name = name,
        nick = "@$nick",
        avatar = avatar,
        hasAccess = !blocked.contains(currentUid)
    )
}
