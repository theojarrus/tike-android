package com.theost.tike.ui.interfaces

import org.threeten.bp.LocalDate

interface EventListener {
    fun onEventCreated(date: LocalDate)
}