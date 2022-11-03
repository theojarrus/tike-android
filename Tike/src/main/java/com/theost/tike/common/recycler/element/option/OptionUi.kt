package com.theost.tike.common.recycler.element.option

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.domain.model.multi.OptionAction

data class OptionUi(
    @StringRes val title: Int,
    val content: String,
    @DrawableRes val icon: Int,
    val action: OptionAction?
): DelegateItem {
    override fun id(): String = "$title"
    override fun content(): String = content + icon + action
}
