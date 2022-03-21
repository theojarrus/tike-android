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
    val creatorId: String = "",
    val participants: List<String> = emptyList(),
    val participantsLimit: Int = 0,
    val created: Int = 0,
    val modified: Int = 0,
    val weekDay: Int = 0,
    val monthDay: Int = 0,
    val month: Int = 0,
    val year: Int = 0,
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
        date = LocalDate.of(year, month, monthDay),
        beginTime = LocalTime.ofNanoOfDay(beginTime),
        endTime = LocalTime.ofNanoOfDay(endTime)
    )
}