package com.theost.tike.feature.schedule.ui.recycler.decorator

import android.graphics.drawable.Drawable
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

/** Shows background of selected date **/
class DayDecorator(val drawable: Drawable?) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean = true
    override fun decorate(view: DayViewFacade) {
        drawable?.let { view.setSelectionDrawable(drawable) }
    }
}
