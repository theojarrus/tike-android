package com.theost.tike.domain.model.dto

import com.google.firebase.firestore.DocumentId
import com.theost.tike.domain.model.core.Event
import com.theost.tike.domain.model.multi.EventType.PROPER
import com.theost.tike.domain.model.multi.EventType.REFERENCE
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

data class EventDto(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val creatorId: String = "",
    val requesting: List<String> = emptyList(),
    val pending: List<String> = emptyList(),
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
    val repeatMode: String = "",
    val locationAddress: String? = null,
    val locationLatitude: Double? = null,
    val locationLongitude: Double? = null
)

fun EventDto.mapToEvent(type: String = PROPER.name): Event {
    return Event(
        id = id,
        title = title,
        description = description,
        creatorId = creatorId,
        requesting = requesting,
        pending = pending,
        participants = participants,
        participantsLimit = participantsLimit,
        date = LocalDate.of(year, month, monthDay),
        beginTime = LocalTime.ofNanoOfDay(beginTime),
        endTime = LocalTime.ofNanoOfDay(endTime),
        repeatMode = repeatMode,
        locationAddress = locationAddress,
        locationLatitude = locationLatitude,
        locationLongitude = locationLongitude,
        type = if (type == REFERENCE.name) REFERENCE else PROPER
    )
}
