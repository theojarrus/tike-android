package com.theost.tike.domain.model.core.mapper

import com.theost.tike.domain.model.core.Event
import com.theost.tike.domain.model.dto.EventDto
import com.theost.tike.domain.model.multi.EventTypeOld.PROPER
import com.theost.tike.domain.model.multi.EventTypeOld.REFERENCE
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

class EventMapper(private val locationMapper: LocationMapper) : (EventDto, String) -> Event {

    override fun invoke(eventDto: EventDto, type: String): Event = with(eventDto) {
        Event(
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
            location = locationMapper(locationAddress, locationLatitude, locationLongitude),
            type = if (type == REFERENCE.name) REFERENCE else PROPER
        )
    }
}
