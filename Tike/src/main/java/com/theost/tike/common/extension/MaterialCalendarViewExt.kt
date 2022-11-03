package com.theost.tike.common.extension

import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

fun <T : DayViewDecorator> MaterialCalendarView.updateDecorator(decorator: T, block: (T) -> Unit) {
    removeDecorator(decorator)
    block(decorator)
    addDecorator(decorator)
}
