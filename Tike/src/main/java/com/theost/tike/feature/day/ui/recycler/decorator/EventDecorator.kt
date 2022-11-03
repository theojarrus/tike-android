package com.theost.tike.feature.day.ui.recycler.decorator

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import com.theost.tike.domain.model.core.Dates

/** Shows DotSpan color of date with events **/
class EventDecorator(
    private val color: Int,
    var dates: Dates = Dates()
) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.hasDailyEvent
            .or(dates.days.contains(Triple(day.year, day.month, day.day)))
            .or(dates.weekDays.contains(day.date.dayOfWeek.value))
            .or(dates.monthDays.contains(day.day))
            .or(dates.yearDays.contains(Pair(day.month, day.day)))
    }

    override fun decorate(view: DayViewFacade) = view.addSpan(DotSpan(6f, color))
}
