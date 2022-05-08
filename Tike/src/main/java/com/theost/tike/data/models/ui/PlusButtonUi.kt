package com.theost.tike.data.models.ui

import com.theost.tike.ui.interfaces.DelegateItem

class PlusButtonUi(
    val id: String = "add_button",
    val content: String = "add_button"
) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = content
}