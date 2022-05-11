package com.theost.tike.data.models.ui

import com.theost.tike.data.models.core.User
import com.theost.tike.ui.interfaces.DelegateItem

data class UserUi(
    val uid: String,
    val name: String,
    val nick: String,
    val avatar: String,
    val isSelected: Boolean
) : DelegateItem {
    override fun id(): Any = uid
    override fun content(): Any = isSelected
}

fun User.mapToUserUi(): UserUi {
    return UserUi(
        uid = uid,
        name = name,
        nick = "@$nick",
        avatar = avatar,
        isSelected = false
    )
}