package com.theost.tike.data.models.ui

import com.theost.tike.ui.interfaces.DelegateItem

class ListButton() : DelegateItem {
    override fun id(): Any = "add_button"
    override fun content(): Any = "add_button"
}