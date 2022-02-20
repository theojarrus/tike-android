package com.theost.tike.ui.decorators

import android.content.Context
import android.text.style.TextAppearanceSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.theost.tike.R

class TodayDecorator(context: Context) : DayViewDecorator {

    private val span: TextAppearanceSpan = TextAppearanceSpan(context, R.style.Theme_Tike_Date_Today)
    private val today: CalendarDay = CalendarDay.today()

    override fun shouldDecorate(day: CalendarDay): Boolean = today == day

    override fun decorate(view: DayViewFacade) {
        view.addSpan(span)
    }
}