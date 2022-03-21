package com.theost.tike.data.models.ui

import com.theost.tike.ui.interfaces.DelegateItem

data class ListUser(
    val id: String,
    val name: String,
    val nick: String,
    val avatar: String,
    val isSelected: Boolean
) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = isSelected
}