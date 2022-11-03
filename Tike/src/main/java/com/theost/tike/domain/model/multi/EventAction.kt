package com.theost.tike.domain.model.multi

sealed class EventAction(
    val id: String,
    val item: String,
    val creator: String,
) {

    class Info(
        id: String,
        item: String,
        creator: String
    ) : EventAction(id, item, creator)

    class Accept(
        id: String,
        item: String,
        creator: String,
        val uid: String,
        val type: EventType
    ) : EventAction(id, item, creator)

    class Decline(
        id: String,
        item: String,
        creator: String,
        val uid: String?,
        val type: EventType,
    ) : EventAction(id, item, creator)
}
