package com.theost.tike.common.recycler.element.event

import com.theost.tike.common.recycler.element.user.UserUi
import com.theost.tike.common.util.DateUtils.formatBeginEndTime
import com.theost.tike.common.util.DateUtils.formatDate
import com.theost.tike.domain.model.core.Event
import com.theost.tike.domain.model.multi.EventType

class EventUiMapper : (Event, List<UserUi>, EventType, String) -> EventUi {

    override fun invoke(
        event: Event,
        members: List<UserUi>,
        type: EventType,
        auid: String
    ): EventUi = with(event) {
        EventUi(
            id = id,
            title = title,
            description = description,
            creator = creatorId,
            participants = members.filterNot { it.uid == auid },
            time = formatBeginEndTime(
                beginTime.hour,
                beginTime.minute,
                endTime.hour,
                endTime.minute
            ),
            date = formatDate(
                date.year,
                date.monthValue,
                date.dayOfMonth
            ),
            type = type
        )
    }
}
