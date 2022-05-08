package com.theost.tike.data.models.ui

import com.theost.tike.ui.interfaces.DelegateItem

class ListButtonUi(
    val content: String,
    val id: String = "list_button"
) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = content
}