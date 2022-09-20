package com.theost.tike.data.models.ui

import androidx.annotation.DrawableRes
import com.theost.tike.data.models.state.OptionAction
import com.theost.tike.ui.interfaces.DelegateItem

data class OptionUi(
    val title: String,
    val subtitle: String,
    @DrawableRes val icon: Int,
    val action: OptionAction?
): DelegateItem {
    override fun id(): Any = title + subtitle + icon + action
    override fun content(): Any = title + subtitle + icon
}