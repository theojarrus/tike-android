package com.theost.tike.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.theost.tike.R

class SelectionDecorator(context: Context) : DayViewDecorator {
    private val drawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.selector_date_bg)

    override fun shouldDecorate(day: CalendarDay): Boolean = true

    override fun decorate(view: DayViewFacade) {
        if (drawable != null) view.setSelectionDrawable(drawable)
    }

}
