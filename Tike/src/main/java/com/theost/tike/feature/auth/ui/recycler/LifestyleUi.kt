package com.theost.tike.feature.auth.ui.recycler

import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.domain.model.core.Lifestyle

data class LifestyleUi(
    val id: String,
    val text: String,
    val isSelected: Boolean
) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = isSelected
}

fun Lifestyle.mapToLifestyleUi(): LifestyleUi {
    return LifestyleUi(
        id = id,
        text = text,
        isSelected = false
    )
}