package com.theost.tike.widgets

import android.content.Context
import android.content.res.Resources.Theme
import android.util.TypedValue
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import com.theost.tike.R


class EventDecorator(private val context: Context) : DayViewDecorator {
    private val color: Int
        get() {
            val typedValue = TypedValue()
            val theme: Theme = context.theme
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            return typedValue.data
        }

    override fun shouldDecorate(day: CalendarDay): Boolean = true

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(6f, color))
    }

}