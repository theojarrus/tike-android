package com.theost.tike.data.models.ui

import com.theost.tike.data.models.core.Event
import com.theost.tike.ui.interfaces.DelegateItem
import com.theost.tike.utils.DateUtils

data class ListEvent(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val participants: List<ListParticipant>
) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = title + description + time + participants
}

fun Event.mapToListEvent(participants: List<ListParticipant>): ListEvent {
    return ListEvent(
        id = id,
        title = title,
        description = description,
        participants = participants,
        time = DateUtils.formatBeginEndTime(
            beginTime.hour,
            beginTime.minute,
            endTime.hour,
            endTime.minute
        )
    )
}