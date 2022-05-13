package com.theost.tike.data.models.ui

import com.theost.tike.data.models.core.Event
import com.theost.tike.ui.interfaces.DelegateItem
import com.theost.tike.ui.utils.DateUtils.formatBeginEndTime

data class EventUi(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val participants: List<UserUi>
) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = title + description + time + participants
}

fun Event.mapToEventUi(participants: List<UserUi>): EventUi {
    return EventUi(
        id = id,
        title = title,
        description = description,
        participants = participants,
        time = formatBeginEndTime(
            beginTime.hour,
            beginTime.minute,
            endTime.hour,
            endTime.minute
        )
    )
}
