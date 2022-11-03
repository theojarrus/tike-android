package com.theost.tike.common.recycler.element.event

import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.common.recycler.element.user.UserUi
import com.theost.tike.common.util.DateUtils.formatBeginEndTime
import com.theost.tike.common.util.DateUtils.formatDate
import com.theost.tike.domain.model.core.Event
import com.theost.tike.domain.model.multi.Direction.In
import com.theost.tike.domain.model.multi.Direction.Out
import com.theost.tike.domain.model.multi.EventMode
import com.theost.tike.domain.model.multi.EventMode.*
import com.theost.tike.domain.model.multi.EventRole.Author
import com.theost.tike.domain.model.multi.EventRole.Member
import com.theost.tike.domain.model.multi.EventType
import com.theost.tike.domain.model.multi.EventType.*

data class EventUi(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val date: String,
    val creator: String,
    val participants: List<UserUi>,
    val type: EventType
) : DelegateItem {
    override fun id(): String = id + if (type is Pending || type is Requesting) participants else ""
    override fun content(): String = title + description + date + time + participants
}

fun Event.mapToEventUi(participants: List<UserUi>, mode: EventMode): EventUi {
    return EventUi(
        id = id,
        title = title,
        description = description,
        creator = creatorId,
        participants = participants,
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
        type = when (mode) {
            SCHEDULE_PROPER -> Schedule(Author)
            SCHEDULE_REFERENCE -> Schedule(Member)
            REQUESTING_IN -> Requesting(In)
            REQUESTING_OUT -> Requesting(Out)
            PENDING_IN -> Pending(In)
            PENDING_OUT -> Pending(Out)
            JOINING -> Joining
        }
    )
}
