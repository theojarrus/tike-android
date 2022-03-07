package com.theost.tike.data.models.ui

import com.theost.tike.ui.interfaces.DelegateItem

data class ListParticipant(
    val id: Int,
    val name: String,
    val avatar: String
) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = name
}