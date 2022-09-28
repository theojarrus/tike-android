package com.theost.tike.common.recycler.element.option

import androidx.annotation.DrawableRes
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.domain.model.multi.OptionAction

data class OptionUi(
    val title: String,
    val subtitle: String,
    @DrawableRes val icon: Int,
    val action: OptionAction?
): DelegateItem {
    override fun id(): Any = title + subtitle + icon + action
    override fun content(): Any = title + subtitle + icon
}
