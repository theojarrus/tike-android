package com.theost.tike.feature.schedule.presentation

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.theost.tike.core.model.StateStatus
import com.theost.tike.core.presentation.BaseState
import com.theost.tike.domain.model.core.Dates
import org.threeten.bp.LocalDate

data class ScheduleState(
    override val status: StateStatus,
    val dates: Dates,
    val today: LocalDate,
    val day: CalendarDay,
    val position: Int
) : BaseState(status)
