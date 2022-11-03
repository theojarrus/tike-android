package com.theost.tike.common.recycler.element.button

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.domain.model.multi.ButtonStyle

data class ButtonUi<T>(
    val action: T,
    @StringRes val text: Int? = null,
    @DrawableRes val icon: Int? = null,
    val style: ButtonStyle
) : DelegateItem {
    override fun id(): String = "$action"
    override fun content(): String = "$text$icon$style"
}
