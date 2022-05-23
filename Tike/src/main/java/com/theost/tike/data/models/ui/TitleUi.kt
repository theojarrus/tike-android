package com.theost.tike.data.models.ui

import androidx.annotation.StringRes
import com.theost.tike.ui.interfaces.DelegateItem

data class TitleUi(@StringRes val stringRes: Int) : DelegateItem {
    override fun id(): Any = stringRes
    override fun content(): Any = stringRes
}
