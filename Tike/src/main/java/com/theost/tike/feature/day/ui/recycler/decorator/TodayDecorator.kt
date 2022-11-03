package com.theost.tike.feature.day.ui.recycler.decorator

import android.text.style.TextAppearanceSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import org.threeten.bp.LocalDate

/** Shows color of today date **/
class TodayDecorator(
    private val span: TextAppearanceSpan,
    private val today: LocalDate
) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean = day.date == today
    override fun decorate(view: DayViewFacade) = view.addSpan(span)
}
