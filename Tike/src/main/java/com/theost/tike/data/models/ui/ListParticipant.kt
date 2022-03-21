package com.theost.tike.data.models.ui

import com.theost.tike.data.models.core.User
import com.theost.tike.ui.interfaces.DelegateItem
import com.theost.tike.utils.StringUtils

data class ListParticipant(
    val id: String,
    val name: String,
    val avatar: String
) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = name
}

fun User.mapToListParticipant(): ListParticipant {
    return ListParticipant(
        id = id,
        name = name,
        avatar = avatar
    )
}

fun User.mapToListUser(selectedIds: List<String>): ListUser {
    return ListUser(
        id = id,
        name = name,
        nick = nick,
        avatar = avatar,
        isSelected = selectedIds.contains(id)
    )
}