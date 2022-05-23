package com.theost.tike.data.models.core

import com.theost.tike.data.models.state.EventType
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val creatorId: String,
    val requesting: List<String>,
    val pending: List<String>,
    val participants: List<String>,
    val participantsLimit: Int,
    val date: LocalDate,
    val beginTime: LocalTime,
    val endTime: LocalTime,
    val repeatMode: String,
    val type: EventType
)
