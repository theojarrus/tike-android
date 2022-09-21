package com.theost.tike.core.recycler.title

import androidx.annotation.StringRes
import com.theost.tike.common.recycler.delegate.DelegateItem

data class TitleUi(@StringRes val stringRes: Int) : DelegateItem {
    override fun id(): Any = stringRes
    override fun content(): Any = stringRes
}
