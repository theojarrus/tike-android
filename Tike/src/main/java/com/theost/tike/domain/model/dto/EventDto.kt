package com.theost.tike.domain.model.dto

import com.google.firebase.firestore.DocumentId
import com.theost.tike.domain.model.core.Event
import com.theost.tike.domain.model.core.mapper.EventMapper
import com.theost.tike.domain.model.core.mapper.LocationMapper
import com.theost.tike.domain.model.multi.EventTypeOld.PROPER

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
    return EventMapper(LocationMapper()).invoke(this, type)
}
