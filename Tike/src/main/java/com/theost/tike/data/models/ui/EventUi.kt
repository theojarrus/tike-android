package com.theost.tike.data.models.ui

import com.theost.tike.data.models.core.Event
import com.theost.tike.data.models.state.EventMode
import com.theost.tike.ui.interfaces.DelegateItem
import com.theost.tike.ui.utils.DateUtils.formatBeginEndTime

data class EventUi(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val participants: List<UserUi>,
    val mode: EventMode
) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = title + description + time + participants
}

fun Event.mapToEventUi(participants: List<UserUi>, mode: EventMode): EventUi {
    return EventUi(
        id = id,
        title = title,
        description = description,
        participants = participants,
        mode = mode,
        time = formatBeginEndTime(
            beginTime.hour,
            beginTime.minute,
            endTime.hour,
            endTime.minute
        )
    )
}
