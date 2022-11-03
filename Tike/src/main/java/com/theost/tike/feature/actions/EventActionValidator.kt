package com.theost.tike.feature.actions

import com.theost.tike.common.exception.ValidationException
import com.theost.tike.domain.model.core.Event
import com.theost.tike.domain.model.multi.Direction.In
import com.theost.tike.domain.model.multi.Direction.Out
import com.theost.tike.domain.model.multi.EventAction
import com.theost.tike.domain.model.multi.EventAction.Accept
import com.theost.tike.domain.model.multi.EventAction.Decline
import com.theost.tike.domain.model.multi.EventRole.Author
import com.theost.tike.domain.model.multi.EventRole.Member
import com.theost.tike.domain.model.multi.EventType.*
import io.reactivex.Single

class EventActionValidator {

    operator fun invoke(eventAction: EventAction, actionEvent: Event, auid: String): Single<Event> {
        val validation = when (eventAction) {
            is Accept -> validateAcceptAction(eventAction, actionEvent, auid)
            is Decline -> validateDeclineAction(eventAction, actionEvent, auid)
            else -> false
        }
        return if (validation) Single.just(actionEvent) else Single.error(ValidationException())
    }

    private fun validateAcceptAction(
        eventAction: Accept,
        actionEvent: Event,
        auid: String
    ): Boolean {
        return actionEvent.participants.size < actionEvent.participantsLimit && when {
            eventAction.type is Pending && eventAction.type.direction is In -> {
                actionEvent.pending.contains(auid)
            }
            eventAction.type is Requesting && eventAction.type.direction is In -> {
                actionEvent.requesting.contains(eventAction.uid)
            }
            else -> false
        }
    }

    private fun validateDeclineAction(
        eventAction: Decline,
        actionEvent: Event,
        auid: String
    ): Boolean {
        return when {
            eventAction.type is Pending && eventAction.type.direction is In -> {
                actionEvent.pending.contains(auid)
            }
            eventAction.type is Pending && eventAction.type.direction is Out -> {
                actionEvent.pending.contains(eventAction.uid)
            }
            eventAction.type is Requesting && eventAction.type.direction is In -> {
                actionEvent.requesting.contains(eventAction.uid)
            }
            eventAction.type is Requesting && eventAction.type.direction is Out -> {
                actionEvent.requesting.contains(auid)
            }
            eventAction.type is Schedule && eventAction.type.role is Author -> {
                actionEvent.creatorId == auid
            }
            eventAction.type is Schedule && eventAction.type.role is Member -> {
                actionEvent.participants.contains(auid)
            }
            else -> false
        }
    }
}
