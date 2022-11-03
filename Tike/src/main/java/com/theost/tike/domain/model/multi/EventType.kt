package com.theost.tike.domain.model.multi

sealed class EventType {

    class Schedule(val role: EventRole) : EventType()
    class Pending(val direction: Direction) : EventType()
    class Requesting(val direction: Direction) : EventType()

    object Joining : EventType()
}
