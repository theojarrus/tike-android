package com.theost.tike.data.models.core

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val creatorId: Int,
    val participants: List<Int>,
    val participantsLimit: Int,
    val date: LocalDate,
    val beginTime: LocalTime,
    val endTime: LocalTime,
)