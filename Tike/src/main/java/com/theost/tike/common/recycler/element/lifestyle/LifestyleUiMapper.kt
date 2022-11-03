package com.theost.tike.common.recycler.element.lifestyle

import com.theost.tike.domain.model.core.Lifestyle

class LifestyleUiMapper : (Lifestyle) -> LifestyleUi {

    override fun invoke(lifestyle: Lifestyle): LifestyleUi = with(lifestyle) {
        LifestyleUi(
            id = id,
            text = text,
            isSelected = false
        )
    }
}
