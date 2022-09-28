package com.theost.tike.feature.auth.business

import com.theost.tike.common.extension.mapList
import com.theost.tike.common.recycler.element.lifestyle.LifestyleToLifestyleUiMapper
import com.theost.tike.common.recycler.element.lifestyle.LifestyleUi
import com.theost.tike.domain.repository.LifestylesRepository
import io.reactivex.Single

class FetchLifestyles(
    private val lifestylesRepository: LifestylesRepository,
    private val mapper: LifestyleToLifestyleUiMapper
) {

    operator fun invoke(): Single<List<LifestyleUi>> {
        return lifestylesRepository.getLifestyles()
            .mapList { mapper(this) }
    }
}
