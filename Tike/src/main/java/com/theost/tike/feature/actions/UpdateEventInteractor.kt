package com.theost.tike.feature.actions

import com.theost.tike.common.exception.UnsupportedException
import com.theost.tike.domain.model.core.Event
import com.theost.tike.domain.model.multi.Direction.In
import com.theost.tike.domain.model.multi.Direction.Out
import com.theost.tike.domain.model.multi.EventAction
import com.theost.tike.domain.model.multi.EventAction.Accept
import com.theost.tike.domain.model.multi.EventAction.Decline
import com.theost.tike.domain.model.multi.EventRole.Author
import com.theost.tike.domain.model.multi.EventRole.Member
import com.theost.tike.domain.model.multi.EventType.*
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.EventsRepository
import io.reactivex.Completable

class UpdateEventInteractor(
    private val authRepository: AuthRepository,
    private val eventsRepository: EventsRepository,
    private val eventActionValidator: EventActionValidator
) {

    operator fun invoke(eventAction: EventAction): Completable {
        return authRepository.getActualUser()
            .flatMapCompletable { firebaseUser ->
                eventsRepository.getRemoteEvent(eventAction.creator, eventAction.id)
                    .flatMap { event -> eventActionValidator(eventAction, event, firebaseUser.uid) }
                    .flatMapCompletable { actionEvent ->
                        when (eventAction) {
                            is Accept -> getAcceptEventCompletable(
                                auid = firebaseUser.uid,
                                eventAction = eventAction,
                                actionEvent = actionEvent
                            )
                            is Decline -> getDeclineEventCompletable(
                                auid = firebaseUser.uid,
                                eventAction = eventAction,
                                actionEvent = actionEvent
                            )
                            else -> Completable.error(UnsupportedException())
                        }
                    }
            }
    }

    private fun getAcceptEventCompletable(
        eventAction: Accept,
        actionEvent: Event,
        auid: String
    ): Completable {
        return when  {
            eventAction.type is Pending && eventAction.type.direction is In -> {
                eventsRepository.addReferenceFromPendingActiveEvent(
                    id = actionEvent.id,
                    creator = actionEvent.creatorId,
                    pending = auid,
                    participants = actionEvent.participants
                )
            }
            eventAction.type is Requesting && eventAction.type.direction is In -> {
                eventsRepository.addReferenceFromRequestingActiveEvent(
                    id = actionEvent.id,
                    creator = actionEvent.creatorId,
                    requesting = eventAction.uid,
                    participants = actionEvent.participants
                )
            }
            else -> Completable.error(UnsupportedException())
        }
    }

    private fun getDeclineEventCompletable(
        eventAction: Decline,
        actionEvent: Event,
        auid: String
    ): Completable {
        return when {
            eventAction.type is Pending && eventAction.type.direction is In -> {
                eventsRepository.deleteReferencePendingEvent(
                    id = actionEvent.id,
                    creator = actionEvent.creatorId,
                    pending = auid,
                    participantsLimit = actionEvent.participantsLimit
                )
            }
            eventAction.type is Pending && eventAction.type.direction is Out -> {
                eventsRepository.deleteReferencePendingEvent(
                    id = actionEvent.id,
                    creator = actionEvent.creatorId,
                    pending = eventAction.uid.orEmpty(),
                    participantsLimit = actionEvent.participantsLimit
                )
            }
            eventAction.type is Requesting && eventAction.type.direction is In -> {
                eventsRepository.deleteReferenceRequestingEvent(
                    id = actionEvent.id,
                    creator = actionEvent.creatorId,
                    requesting = eventAction.uid.orEmpty()
                )
            }
            eventAction.type is Requesting && eventAction.type.direction is Out -> {
                eventsRepository.deleteReferenceRequestingEvent(
                    id = actionEvent.id,
                    creator = actionEvent.creatorId,
                    requesting = auid
                )
            }
            eventAction.type is Schedule && eventAction.type.role is Author -> {
                eventsRepository.deleteProperEvent(
                    id = actionEvent.id,
                    uid = auid,
                    participants = actionEvent.participants
                )
            }
            eventAction.type is Schedule && eventAction.type.role is Member -> {
                eventsRepository.deleteReferenceEvent(
                    id = actionEvent.id,
                    uid = auid,
                    creator = actionEvent.creatorId,
                    participantsLimit = actionEvent.participantsLimit
                )
            }
            else -> Completable.error(UnsupportedException())
        }
    }
}
