package com.theost.tike.domain.model.core

data class Dates(
    val days: List<Triple<Int, Int, Int>> = emptyList(),
    val yearDays: List<Pair<Int, Int>> = emptyList(),
    val monthDays: List<Int> = emptyList(),
    val weekDays: List<Int> = emptyList(),
    val hasDailyEvent: Boolean = false
)