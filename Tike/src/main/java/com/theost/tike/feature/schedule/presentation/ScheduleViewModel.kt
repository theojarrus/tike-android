package com.theost.tike.feature.schedule.presentation

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.core.model.StateStatus.Success
import com.theost.tike.core.presentation.BaseStateViewModel
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.DatesRepository
import com.theost.tike.feature.schedule.business.ObserveDatesInteractor

class ScheduleViewModel : BaseStateViewModel<ScheduleState>() {

    private val observeDatesInteractor = ObserveDatesInteractor(
        AuthRepository,
        DatesRepository
    )

    fun observeDates() {
        disposableSwitch {
            observeDatesInteractor()
                .subscribe({ dates ->
                    update { copy(status = Success, dates = dates) }
                }, { error ->
                    log(this, error)
                })
        }
    }

    fun changeDay(day: CalendarDay, position: Int) {
        update { copy(day = day, position = position) }
    }
}
