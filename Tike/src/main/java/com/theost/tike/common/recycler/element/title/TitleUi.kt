package com.theost.tike.common.recycler.element.title

import androidx.annotation.StringRes
import com.theost.tike.common.recycler.delegate.DelegateItem

data class TitleUi(@StringRes val text: Int) : DelegateItem {
    override fun id(): String = "$text"
    override fun content(): String = "$text"
}
