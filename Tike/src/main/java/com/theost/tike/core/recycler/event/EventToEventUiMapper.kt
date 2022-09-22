package com.theost.tike.core.recycler.event

import com.theost.tike.common.util.DateUtils
import com.theost.tike.core.recycler.user.UserUi
import com.theost.tike.domain.model.core.Event
import com.theost.tike.domain.model.multi.EventMode

class EventToEventUiMapper : (Event, List<UserUi>, EventMode) -> EventUi {

    override fun invoke(
        event: Event,
        members: List<UserUi>,
        mode: EventMode
    ): EventUi = with(event) {
        EventUi(
            id = id,
            title = title,
            description = description,
            creator = creatorId,
            participants = members,
            mode = mode,
            time = DateUtils.formatBeginEndTime(
                beginTime.hour,
                beginTime.minute,
                endTime.hour,
                endTime.minute
            ),
            date = DateUtils.formatDate(
                date.year,
                date.monthValue,
                date.dayOfMonth
            )
        )
    }
}
