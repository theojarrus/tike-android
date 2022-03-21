package com.theost.tike.data.models.ui

import com.theost.tike.ui.interfaces.DelegateItem

class ListButton(val text: String) : DelegateItem {
    override fun id(): Any = text
    override fun content(): Any = text
}