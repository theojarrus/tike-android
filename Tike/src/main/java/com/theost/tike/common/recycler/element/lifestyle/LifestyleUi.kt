package com.theost.tike.common.recycler.element.lifestyle

import com.theost.tike.common.recycler.delegate.DelegateItem

data class LifestyleUi(
    val id: String,
    val text: String,
    val isSelected: Boolean
) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = isSelected
}
