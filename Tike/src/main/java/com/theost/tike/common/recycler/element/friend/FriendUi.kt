package com.theost.tike.common.recycler.element.friend

import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.domain.model.multi.Direction

data class FriendUi(
    val uid: String,
    val name: String?,
    val nick: String?,
    val avatar: String?,
    val isActive: Boolean,
    val hasAccess: Boolean,
    val direction: Direction
) : DelegateItem {
    override fun id(): String = uid
    override fun content(): String = name + nick + avatar + isActive + hasAccess + direction
}
