package com.theost.tike.ui.decorators

import android.content.Context
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import com.theost.tike.R

// Changes DotSpan color of selected date
class SelectedEventDecorator(context: Context, private var day: CalendarDay? = null) : DayViewDecorator {
    private val color: Int = ContextCompat.getColor(context, R.color.white)

    fun setDay(day: CalendarDay?) {
        this.day = day
    }

    override fun shouldDecorate(day: CalendarDay): Boolean = false // this.day != null && this.day == day

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(6f, color))
    }

}