package com.theost.tike.feature.day.business

import com.theost.tike.common.extension.mapList
import com.theost.tike.common.recycler.element.event.EventUi
import com.theost.tike.common.recycler.element.event.EventUiMapper
import com.theost.tike.common.recycler.element.user.UserUiMapper
import com.theost.tike.domain.model.multi.EventRole.Author
import com.theost.tike.domain.model.multi.EventRole.Member
import com.theost.tike.domain.model.multi.EventType.Schedule
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.EventsRepository
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.LocalDate

class ObserveDayEventsInteractor(
    private val authRepository: AuthRepository,
    private val eventsRepository: EventsRepository,
    private val usersRepository: UsersRepository,
    private val eventMapper: EventUiMapper,
    private val userMapper: UserUiMapper
) {

    operator fun invoke(date: LocalDate): Observable<List<EventUi>> {
        return authRepository.getActualUser()
            .flatMapObservable { firebaseUser ->
                eventsRepository.observeEvents(
                    uid = firebaseUser.uid,
                    year = date.year,
                    month = date.monthValue,
                    dayOfMonth = date.dayOfMonth,
                    dayOfWeek = date.dayOfWeek.value
                ).switchMapSingle { events ->
                    if (events.isNotEmpty()) {
                        Single.zip(
                            events.map { event ->
                                usersRepository.getUsers(event.participants)
                                    .mapList { userMapper(it, firebaseUser.uid) }
                                    .map { members ->
                                        eventMapper(
                                            event = event,
                                            members = members,
                                            type = getEventType(firebaseUser.uid, event.creatorId),
                                            auid = firebaseUser.uid
                                        )
                                    }
                            }
                        ) { it.filterIsInstance<EventUi>() }
                    } else {
                        Single.just(emptyList())
                    }
                }
            }
    }

    private fun getEventType(auid: String, creator: String): Schedule {
        val role = if (auid == creator) Author else Member
        return Schedule(role)
    }
}
