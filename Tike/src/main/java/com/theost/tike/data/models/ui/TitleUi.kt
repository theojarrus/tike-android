package com.theost.tike.data.models.ui

import com.theost.tike.ui.interfaces.DelegateItem

data class TitleUi(val text: String) : DelegateItem {
    override fun id(): Any = text
    override fun content(): Any = text
}