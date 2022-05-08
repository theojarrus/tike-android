package com.theost.tike.data.repositories

import com.androidhuman.rxfirebase2.firestore.RxFirebaseFirestore
import com.theost.tike.data.api.FirestoreApi.provideProperEventsCollection
import com.theost.tike.data.models.core.Dates
import com.theost.tike.data.models.core.Event
import com.theost.tike.data.models.dto.EventDto
import com.theost.tike.data.models.dto.mapToEvent
import com.theost.tike.data.models.state.RepeatMode.*
import com.theost.tike.ui.extensions.getOrNull
import com.theost.tike.ui.extensions.isFalse
import com.theost.tike.ui.extensions.toLongInt
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

object EventsRepository {

    fun observeProperEventsDates(uid: String): Observable<Dates> {
        return observeProperRepeatedDayEventsDates(uid).switchMap { hasDailyEvent ->
            if (!hasDailyEvent) {
                Observable.combineLatest(
                    observeProperDateEventsDates(uid),
                    observeProperRepeatedYearEventsDates(uid),
                    observeProperRepeatedMonthEventsDates(uid),
                    observeProperRepeatedWeekEventsDates(uid)
                ) { days, yearDays, monthDays, weekDays ->
                    Dates(
                        days = days,
                        yearDays = yearDays,
                        monthDays = monthDays,
                        weekDays = weekDays
                    )
                }
            } else {
                Observable.just(Dates(hasDailyEvent = hasDailyEvent))
            }
        }
    }

    private fun observeProperDateEventsDates(uid: String): Observable<List<Triple<Int, Int, Int>>> {
        return RxFirebaseFirestore.dataChanges(
            provideProperEventsCollection(uid)
                .whereEqualTo(EventDto::repeatMode.name, NEVER)
        ).map { value ->
            value.getOrNull()?.map { snapshot ->
                snapshot.data.run {
                    Triple(
                        get(EventDto::year.name).toLongInt(),
                        get(EventDto::month.name).toLongInt(),
                        get(EventDto::monthDay.name).toLongInt()
                    )
                }
            } ?: emptyList()
        }.subscribeOn(Schedulers.io())
    }

    private fun observeProperRepeatedYearEventsDates(uid: String): Observable<List<Pair<Int, Int>>> {
        return RxFirebaseFirestore.dataChanges(
            provideProperEventsCollection(uid)
                .whereEqualTo(EventDto::repeatMode.name, YEAR.name)
        ).map { value ->
            value.getOrNull()?.map { snapshot ->
                snapshot.data.run {
                    Pair(
                        get(EventDto::month.name).toLongInt(),
                        get(EventDto::monthDay.name).toLongInt()
                    )
                }
            } ?: emptyList()
        }.subscribeOn(Schedulers.io())
    }

    private fun observeProperRepeatedMonthEventsDates(uid: String): Observable<List<Int>> {
        return RxFirebaseFirestore.dataChanges(
            provideProperEventsCollection(uid)
                .whereEqualTo(EventDto::repeatMode.name, MONTH.name)
        ).map { it.getOrNull()?.map { it.get(EventDto::monthDay.name).toLongInt() } ?: emptyList() }
            .subscribeOn(Schedulers.io())
    }

    private fun observeProperRepeatedWeekEventsDates(uid: String): Observable<List<Int>> {
        return RxFirebaseFirestore.dataChanges(
            provideProperEventsCollection(uid)
                .whereEqualTo(EventDto::repeatMode.name, WEEK.name)
        ).map { value ->
            value.getOrNull()?.map { it.get(EventDto::weekDay.name).toLongInt() } ?: emptyList()
        }.subscribeOn(Schedulers.io())
    }

    private fun observeProperRepeatedDayEventsDates(uid: String): Observable<Boolean> {
        return RxFirebaseFirestore.dataChanges(
            provideProperEventsCollection(uid)
                .whereEqualTo(EventDto::repeatMode.name, DAY.name)
        ).map { it.getOrNull()?.isEmpty.isFalse() }
            .subscribeOn(Schedulers.io())
    }

    fun observeProperEvents(
        uid: String,
        year: Int,
        month: Int,
        dayOfMonth: Int,
        dayOfWeek: Int
    ): Observable<List<Event>> {
        return Observable.combineLatest(
            observeProperDateEvents(uid, year, month, dayOfMonth),
            observeProperRepeatedEvents(uid, month, dayOfMonth, dayOfWeek)
        ) { dateEvents, repeatedEvents ->
            (dateEvents + repeatedEvents)
                .map { it.mapToEvent() }
                .sortedBy { it.endTime }
                .sortedBy { it.beginTime }
        }.subscribeOn(Schedulers.io())
    }

    private fun observeProperDateEvents(
        uid: String,
        year: Int,
        month: Int,
        dayOfMonth: Int
    ): Observable<List<EventDto>> {
        return RxFirebaseFirestore.dataChanges(
            provideProperEventsCollection(uid)
                .whereEqualTo(EventDto::repeatMode.name, NEVER)
                .whereEqualTo(EventDto::year.name, year)
                .whereEqualTo(EventDto::month.name, month)
                .whereEqualTo(EventDto::monthDay.name, dayOfMonth)
        ).map { it.getOrNull()?.toObjects(EventDto::class.java) ?: emptyList() }
            .subscribeOn(Schedulers.io())
    }

    private fun observeProperRepeatedEvents(
        uid: String,
        month: Int,
        dayOfMonth: Int,
        dayOfWeek: Int
    ): Observable<List<EventDto>> {
        return Observable.combineLatest(
            observeProperRepeatedYearEvents(uid, month, dayOfMonth),
            observeProperRepeatedMonthEvents(uid, dayOfMonth),
            observeProperRepeatedWeekEvents(uid, dayOfWeek),
            observeProperRepeatedDayEvents(uid)
        ) { yearEvents, monthEvents, weekEvents, dayEvents ->
            yearEvents + monthEvents + weekEvents + dayEvents
        }.subscribeOn(Schedulers.io())
    }

    private fun observeProperRepeatedYearEvents(
        uid: String,
        month: Int,
        dayOfMonth: Int
    ): Observable<List<EventDto>> {
        return RxFirebaseFirestore.dataChanges(
            provideProperEventsCollection(uid)
                .whereEqualTo(EventDto::repeatMode.name, YEAR.name)
                .whereEqualTo(EventDto::month.name, month)
                .whereEqualTo(EventDto::monthDay.name, dayOfMonth)
        ).map { it.getOrNull()?.toObjects(EventDto::class.java) ?: emptyList() }
            .subscribeOn(Schedulers.io())
    }

    private fun observeProperRepeatedMonthEvents(
        uid: String,
        dayOfMonth: Int
    ): Observable<List<EventDto>> {
        return RxFirebaseFirestore.dataChanges(
            provideProperEventsCollection(uid)
                .whereEqualTo(EventDto::repeatMode.name, MONTH.name)
                .whereEqualTo(EventDto::monthDay.name, dayOfMonth)
        ).map { it.getOrNull()?.toObjects(EventDto::class.java) ?: emptyList() }
            .subscribeOn(Schedulers.io())
    }

    private fun observeProperRepeatedWeekEvents(
        uid: String,
        dayOfWeek: Int
    ): Observable<List<EventDto>> {
        return RxFirebaseFirestore.dataChanges(
            provideProperEventsCollection(uid)
                .whereEqualTo(EventDto::repeatMode.name, WEEK.name)
                .whereEqualTo(EventDto::weekDay.name, dayOfWeek)
        ).map { it.getOrNull()?.toObjects(EventDto::class.java) ?: emptyList() }
            .subscribeOn(Schedulers.io())
    }

    private fun observeProperRepeatedDayEvents(
        uid: String
    ): Observable<List<EventDto>> {
        return RxFirebaseFirestore.dataChanges(
            provideProperEventsCollection(uid)
                .whereEqualTo(EventDto::repeatMode.name, DAY.name)
        ).map { it.getOrNull()?.toObjects(EventDto::class.java) ?: emptyList() }
            .subscribeOn(Schedulers.io())
    }

    fun addEvent(
        uid: String,
        title: String,
        description: String,
        participants: List<String>,
        participantsLimit: Int,
        created: Int,
        modified: Int,
        weekDay: Int,
        monthDay: Int,
        month: Int,
        year: Int,
        beginTime: Long,
        endTime: Long,
        repeatMode: String
    ): Completable {
        return RxFirebaseFirestore.set(
            provideProperEventsCollection(uid).document(),
            EventDto(
                title = title,
                description = description,
                creatorId = uid,
                participants = participants,
                participantsLimit = participantsLimit,
                created = created,
                modified = modified,
                weekDay = weekDay,
                monthDay = monthDay,
                month = month,
                year = year,
                beginTime = beginTime,
                endTime = endTime,
                repeatMode = repeatMode
            )
        ).subscribeOn(Schedulers.io())
    }
}