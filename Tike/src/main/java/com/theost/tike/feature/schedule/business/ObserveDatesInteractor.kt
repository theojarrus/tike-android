package com.theost.tike.feature.schedule.business

import com.theost.tike.domain.model.core.Dates
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.DatesRepository
import io.reactivex.Observable

class ObserveDatesInteractor(
    private val authRepository: AuthRepository,
    private val datesRepository: DatesRepository
) {

    operator fun invoke(): Observable<Dates> {
        return authRepository.getActualUser()
            .flatMapObservable { datesRepository.observeEventsDates(it.uid) }
    }
}
