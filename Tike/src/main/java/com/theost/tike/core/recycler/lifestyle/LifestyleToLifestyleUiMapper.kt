package com.theost.tike.core.recycler.lifestyle

import com.theost.tike.domain.model.core.Lifestyle

class LifestyleToLifestyleUiMapper : (Lifestyle) -> LifestyleUi {

    override fun invoke(lifestyle: Lifestyle): LifestyleUi = with(lifestyle) {
        LifestyleUi(
            id = id,
            text = text,
            isSelected = false
        )
    }
}
