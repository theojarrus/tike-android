package com.theost.tike.common.recycler.element.title

import androidx.annotation.StringRes
import com.theost.tike.common.recycler.delegate.DelegateItem

data class TitleUi(@StringRes val stringRes: Int) : DelegateItem {
    override fun id(): Any = stringRes
    override fun content(): Any = stringRes
}
