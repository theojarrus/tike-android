package com.theost.tike.feature.inbox.business

import com.theost.tike.common.extension.filterList
import com.theost.tike.common.extension.mapList
import com.theost.tike.common.recycler.element.event.EventUi
import com.theost.tike.common.recycler.element.event.EventUiMapper
import com.theost.tike.common.recycler.element.user.UserUiMapper
import com.theost.tike.domain.model.core.Event
import com.theost.tike.domain.model.multi.Direction.In
import com.theost.tike.domain.model.multi.Direction.Out
import com.theost.tike.domain.model.multi.EventType
import com.theost.tike.domain.model.multi.EventType.Pending
import com.theost.tike.domain.model.multi.EventType.Requesting
import com.theost.tike.domain.repository.EventsRepository
import com.theost.tike.domain.repository.UsersRepository
import com.theost.tike.feature.inbox.model.InboxList
import io.reactivex.Observable
import io.reactivex.Single
import kotlin.reflect.KProperty1

class ObserveInboxEventsInteractor(
    private val eventsRepository: EventsRepository,
    private val usersRepository: UsersRepository,
    private val eventMapper: EventUiMapper,
    private val userMapper: UserUiMapper
) {

    operator fun invoke(auid: String, type: EventType): Observable<InboxList<EventUi>> {
        return Observable.concat(
            Observable.just(InboxList()),
            getEvents(auid, type).map(::InboxList)
        )
    }

    private fun getEvents(auid: String, type: EventType): Observable<List<EventUi>> {
        return when {
            type is Pending && type.direction is In -> {
                getInEventsObservable(
                    auid = auid,
                    type = type,
                    source = eventsRepository.observeReferencePendingEvents(auid)
                )
            }
            type is Pending && type.direction is Out -> {
                getOutEventsObservable(
                    auid = auid,
                    type = type,
                    source = eventsRepository.observeProperPendingEvents(auid),
                    users = Event::pending
                )
            }
            type is Requesting && type.direction is In -> {
                getOutEventsObservable(
                    auid = auid,
                    type = type,
                    source = eventsRepository.observeProperRequestingEvents(auid),
                    users = Event::requesting
                )
            }
            type is Requesting && type.direction is Out -> {
                getInEventsObservable(
                    auid = auid,
                    type = type,
                    source = eventsRepository.observeReferenceRequestingEvents(auid)
                )
            }
            else -> Observable.just(emptyList())
        }
    }

    private fun getInEventsObservable(
        auid: String,
        type: EventType,
        source: Observable<List<Event>>
    ): Observable<List<EventUi>> {
        return source
            .map { events -> events.filter { it.participants.size < it.participantsLimit } }
            .switchMapSingle { events ->
                if (events.isNotEmpty()) {
                    Single.zip(getInEventsSingles(auid, type, events)) {
                        it.filterIsInstance<EventUi>()
                    }
                } else {
                    Single.just(emptyList())
                }
            }
    }

    private fun getInEventsSingles(
        auid: String,
        type: EventType,
        events: List<Event>
    ): List<Single<EventUi>> {
        return events.map { event ->
            usersRepository.getUser(event.creatorId)
                .map { userMapper(it, auid) }
                .map { user -> eventMapper(event, listOf(user), type, auid) }
        }
    }

    private fun getOutEventsObservable(
        auid: String,
        type: EventType,
        source: Observable<List<Event>>,
        users: KProperty1<Event, List<String>>
    ): Observable<List<EventUi>> {
        return source
            .map { events -> events.filter { it.participants.size < it.participantsLimit } }
            .switchMapSingle { events ->
                if (events.isNotEmpty()) {
                    Single.zip(getOutEventsSingles(auid, type, users, events)) {
                        buildList { it.filterIsInstance<List<EventUi>>().forEach { addAll(it) } }
                    }
                } else {
                    Single.just(emptyList())
                }
            }
    }

    private fun getOutEventsSingles(
        auid: String,
        type: EventType,
        users: KProperty1<Event, List<String>>,
        events: List<Event>
    ): List<Single<List<EventUi>>> {
        return events.map { event ->
            usersRepository.getUsers(users.get(event))
                .filterList { it.uid != auid }
                .mapList { userMapper(it, auid) }
                .mapList { user -> eventMapper(event, listOf(user), type, auid) }
        }
    }
}
