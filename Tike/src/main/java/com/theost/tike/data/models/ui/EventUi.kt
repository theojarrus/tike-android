package com.theost.tike.data.models.ui

import com.theost.tike.data.models.core.Event
import com.theost.tike.data.models.state.EventMode
import com.theost.tike.data.models.state.EventMode.SCHEDULE_PROPER
import com.theost.tike.ui.interfaces.DelegateItem
import com.theost.tike.ui.utils.DateUtils.formatBeginEndTime
import com.theost.tike.ui.utils.DateUtils.formatDate

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
