package com.theost.tike.ui.interfaces

import org.threeten.bp.LocalDate

interface CalendarHolder {
    fun getPendingDate(): LocalDate?
}