package com.theost.tike.data.models.dto

import com.google.firebase.firestore.DocumentId
import com.theost.tike.data.models.core.Event
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

data class EventDto(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val creatorId: Int = 0,
    val participants: List<Int> = emptyList(),
    val participantsLimit: Int = 0,
    val date: Long = 0,
    val beginTime: Long = 0,
    val endTime: Long = 0,
    val repeatMode: String = ""
)

fun EventDto.mapToEvent(): Event {
    return Event(
        id = id,
        title = title,
        description = description,
        creatorId = creatorId,
        participants = participants,
        participantsLimit = participantsLimit,
        date = LocalDate.ofEpochDay(date),
        beginTime = LocalTime.ofNanoOfDay(beginTime),
        endTime = LocalTime.ofNanoOfDay(endTime)
    )
}