package com.theost.tike.common.recycler.element.event

import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.common.recycler.element.user.UserUi
import com.theost.tike.common.util.DateUtils.formatBeginEndTime
import com.theost.tike.common.util.DateUtils.formatDate
import com.theost.tike.domain.model.core.Event
import com.theost.tike.domain.model.multi.EventMode
import com.theost.tike.domain.model.multi.EventMode.SCHEDULE_PROPER

data class EventUi(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val date: String,
    val creator: String,
    val participants: List<UserUi>,
    val mode: EventMode
) : DelegateItem {
    override fun id(): Any = id + if (mode != SCHEDULE_PROPER) participants else ""
    override fun content(): Any = title + description + date + time + participants
}

fun Event.mapToEventUi(participants: List<UserUi>, mode: EventMode): EventUi {
    return EventUi(
        id = id,
        title = title,
        description = description,
        creator = creatorId,
        participants = participants,
        mode = mode,
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
        )
    )
}
