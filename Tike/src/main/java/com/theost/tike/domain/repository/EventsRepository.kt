package com.theost.tike.domain.repository

import com.androidhuman.rxfirebase2.firestore.RxFirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue.arrayRemove
import com.theost.tike.common.extension.append
import com.theost.tike.common.extension.getOrNull
import com.theost.tike.domain.api.FirestoreApi.provideProperEventsCollection
import com.theost.tike.domain.api.FirestoreApi.provideReferenceEventsCollection
import com.theost.tike.domain.model.core.Event
import com.theost.tike.domain.model.dto.EventDto
import com.theost.tike.domain.model.dto.EventReferenceDto
import com.theost.tike.domain.model.dto.mapToEvent
import com.theost.tike.domain.model.multi.EventType.PROPER
import com.theost.tike.domain.model.multi.EventType.REFERENCE
import com.theost.tike.domain.model.multi.ReferenceType.*
import com.theost.tike.domain.model.multi.RepeatMode.*
import com.theost.tike.domain.model.multi.Source.Empty
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object EventsRepository {

    fun observeEvents(
        uid: String,
        year: Int,
        month: Int,
        dayOfMonth: Int,
        dayOfWeek: Int
    ): Observable<List<Event>> {
        return Observable.combineLatest(
            observeProperEvents(uid, year, month, dayOfMonth, dayOfWeek),
            observeReferenceEvents(uid, year, month, dayOfMonth, dayOfWeek)
        ) { properEvents, referenceEvents ->
            (properEvents + referenceEvents)
                .sortedBy { it.endTime }
                .sortedBy { it.beginTime }
        }
    }

    private fun observeReferenceEvents(
        uid: String,
        year: Int,
        month: Int,
        dayOfMonth: Int,
        dayOfWeek: Int
    ): Observable<List<Event>> {
        return Observable.combineLatest(
            observeReferenceDateEvents(uid, year, month, dayOfMonth),
            observeReferenceRepeatedEvents(uid, month, dayOfMonth, dayOfWeek)
        ) { dateEvents, repeatedEvents ->
            (dateEvents + repeatedEvents).map { it.mapToEvent(REFERENCE.name) }
        }
    }

    private fun observeReferenceDateEvents(
        uid: String,
        year: Int,
        month: Int,
        dayOfMonth: Int
    ): Observable<List<EventDto>> {
        return RxFirebaseFirestore.dataChanges(
            provideReferenceEventsCollection(uid)
                .whereEqualTo(EventReferenceDto::type.name, ACTIVE)
                .whereEqualTo(EventReferenceDto::repeatMode.name, NEVER)
                .whereEqualTo(EventReferenceDto::year.name, year)
                .whereEqualTo(EventReferenceDto::month.name, month)
                .whereEqualTo(EventReferenceDto::monthDay.name, dayOfMonth)
        ).map { it.getOrNull()?.toObjects(EventReferenceDto::class.java) ?: emptyList() }
            .switchMap { events ->
                if (events.isNotEmpty()) {
                    observeProperEvents(events.map { it.reference })
                } else {
                    Observable.just(emptyList())
                }
            }
            .subscribeOn(Schedulers.io())
    }

    private fun observeReferenceRepeatedEvents(
        uid: String,
        month: Int,
        dayOfMonth: Int,
        dayOfWeek: Int
    ): Observable<List<EventDto>> {
        return Observable.combineLatest(
            observeReferenceRepeatedYearEvents(uid, month, dayOfMonth),
            observeReferenceRepeatedMonthEvents(uid, dayOfMonth),
            observeReferenceRepeatedWeekEvents(uid, dayOfWeek),
            observeReferenceRepeatedDayEvents(uid)
        ) { yearEvents, monthEvents, weekEvents, dayEvents ->
            yearEvents + monthEvents + weekEvents + dayEvents
        }
    }

    private fun observeReferenceRepeatedYearEvents(
        uid: String,
        month: Int,
        dayOfMonth: Int
    ): Observable<List<EventDto>> {
        return RxFirebaseFirestore.dataChanges(
            provideReferenceEventsCollection(uid)
                .whereEqualTo(EventReferenceDto::type.name, ACTIVE)
                .whereEqualTo(EventReferenceDto::repeatMode.name, YEAR.name)
                .whereEqualTo(EventReferenceDto::month.name, month)
                .whereEqualTo(EventReferenceDto::monthDay.name, dayOfMonth)
        ).map { it.getOrNull()?.toObjects(EventReferenceDto::class.java) ?: emptyList() }
            .switchMap { events ->
                if (events.isNotEmpty()) {
                    observeProperEvents(events.map { it.reference })
                } else {
                    Observable.just(emptyList())
                }
            }
            .subscribeOn(Schedulers.io())
    }

    private fun observeReferenceRepeatedMonthEvents(
        uid: String,
        dayOfMonth: Int
    ): Observable<List<EventDto>> {
        return RxFirebaseFirestore.dataChanges(
            provideReferenceEventsCollection(uid)
                .whereEqualTo(EventReferenceDto::type.name, ACTIVE)
                .whereEqualTo(EventReferenceDto::repeatMode.name, MONTH.name)
                .whereEqualTo(EventReferenceDto::monthDay.name, dayOfMonth)
        ).map { it.getOrNull()?.toObjects(EventReferenceDto::class.java) ?: emptyList() }
            .switchMap { events ->
                if (events.isNotEmpty()) {
                    observeProperEvents(events.map { it.reference })
                } else {
                    Observable.just(emptyList())
                }
            }
            .subscribeOn(Schedulers.io())
    }

    private fun observeReferenceRepeatedWeekEvents(
        uid: String,
        dayOfWeek: Int
    ): Observable<List<EventDto>> {
        return RxFirebaseFirestore.dataChanges(
            provideReferenceEventsCollection(uid)
                .whereEqualTo(EventReferenceDto::type.name, ACTIVE)
                .whereEqualTo(EventReferenceDto::repeatMode.name, WEEK.name)
                .whereEqualTo(EventReferenceDto::weekDay.name, dayOfWeek)
        ).map { it.getOrNull()?.toObjects(EventReferenceDto::class.java) ?: emptyList() }
            .switchMap { events ->
                if (events.isNotEmpty()) {
                    observeProperEvents(events.map { it.reference })
                } else {
                    Observable.just(emptyList())
                }
            }
            .subscribeOn(Schedulers.io())
    }

    private fun observeReferenceRepeatedDayEvents(uid: String): Observable<List<EventDto>> {
        return RxFirebaseFirestore.dataChanges(
            provideReferenceEventsCollection(uid)
                .whereEqualTo(EventReferenceDto::type.name, ACTIVE)
                .whereEqualTo(EventReferenceDto::repeatMode.name, DAY.name)
        ).map { it.getOrNull()?.toObjects(EventReferenceDto::class.java) ?: emptyList() }
            .switchMap { events ->
                if (events.isNotEmpty()) {
                    observeProperEvents(events.map { it.reference })
                } else {
                    Observable.just(emptyList())
                }
            }
            .subscribeOn(Schedulers.io())
    }

    private fun observeProperEvents(references: List<DocumentReference>): Observable<List<EventDto>> {
        return Observable.zip(
            references.map { reference ->
                RxFirebaseFirestore.dataChanges(reference)
                    .map { it.getOrNull()?.toObject(EventDto::class.java) ?: Empty }
            }
        ) { events -> events.filterIsInstance<EventDto>() }
            .subscribeOn(Schedulers.io())
    }

    private fun observeProperEvents(
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
            (dateEvents + repeatedEvents).map { it.mapToEvent(PROPER.name) }
        }
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
        }
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

    private fun observeProperRepeatedDayEvents(uid: String): Observable<List<EventDto>> {
        return RxFirebaseFirestore.dataChanges(
            provideProperEventsCollection(uid)
                .whereEqualTo(EventDto::repeatMode.name, DAY.name)
        ).map { it.getOrNull()?.toObjects(EventDto::class.java) ?: emptyList() }
            .subscribeOn(Schedulers.io())
    }

    fun observeProperVacantEvents(uid: String, id: String): Observable<List<Event>> {
        return RxFirebaseFirestore.dataChanges(
            provideProperEventsCollection(id)
                .whereGreaterThan(EventDto::participantsLimit.name, 1)
        ).map { it.getOrNull()?.toObjects(EventDto::class.java) ?: emptyList() }.map { events ->
            events.asSequence().filter {
                (it.pending.size + it.participants.size < it.participantsLimit)
                    .and(!it.participants.contains(uid))
                    .and(!it.requesting.contains(uid))
                    .and(!it.pending.contains(uid))
            }.map { it.mapToEvent() }
                .sortedBy { it.endTime }
                .sortedBy { it.beginTime }
                .sortedBy { it.date }.toList()
        }.subscribeOn(Schedulers.io())
    }

    fun observeProperPendingEvents(uid: String): Observable<List<Event>> {
        return RxFirebaseFirestore.dataChanges(
            provideProperEventsCollection(uid)
                .whereNotEqualTo(EventDto::pending.name, emptyList<String>())
        ).map { it.getOrNull()?.toObjects(EventDto::class.java) ?: emptyList() }
            .map { events -> events.map { it.mapToEvent() } }
            .subscribeOn(Schedulers.io())
    }

    fun observeProperRequestingEvents(uid: String): Observable<List<Event>> {
        return RxFirebaseFirestore.dataChanges(
            provideProperEventsCollection(uid).whereNotEqualTo(EventDto::requesting.name, 0)
        ).map { it.getOrNull()?.toObjects(EventDto::class.java) ?: emptyList() }.map { events ->
            events.filter { (it.pending.size + it.participants.size < it.participantsLimit) }
                .map { it.mapToEvent() }
        }.subscribeOn(Schedulers.io())
    }

    private fun observeReferenceInactiveEvents(uid: String, type: String): Observable<List<Event>> {
        return RxFirebaseFirestore.dataChanges(
            provideReferenceEventsCollection(uid)
                .whereEqualTo(EventReferenceDto::type.name, type)
        ).map { it.getOrNull()?.toObjects(EventReferenceDto::class.java) ?: emptyList() }
            .switchMap { references ->
                if (references.isNotEmpty()) {
                    Observable.zip(
                        references.map { event ->
                            RxFirebaseFirestore.dataChanges(event.reference).map {
                                it.getOrNull()?.toObject(EventDto::class.java) ?: Empty
                            }
                        }
                    ) { events ->
                        events.filterIsInstance<EventDto>().map { it.mapToEvent() }
                            .sortedBy { it.endTime }
                            .sortedBy { it.beginTime }
                            .sortedBy { it.date }
                    }
                } else {
                    Observable.just(emptyList())
                }
            }.subscribeOn(Schedulers.io())
    }

    fun observeReferenceRequestingEvents(uid: String): Observable<List<Event>> {
        return observeReferenceInactiveEvents(uid, REQUESTING.name)
    }

    fun observeReferencePendingEvents(uid: String): Observable<List<Event>> {
        return observeReferenceInactiveEvents(uid, PENDING.name)
    }

    fun getEvent(uid: String, id: String): Single<Event> {
        return RxFirebaseFirestore.data(provideProperEventsCollection(uid).document(id))
            .map { it.value().toObject(EventDto::class.java) }
            .map { entity -> entity.mapToEvent() }
            .subscribeOn(Schedulers.io())
    }

    fun deleteProperEvent(uid: String, id: String, participants: List<String>): Completable {
        return Observable.fromIterable(participants).flatMapCompletable {
            RxFirebaseFirestore.delete(provideReferenceEventsCollection(it).document(id))
        }.andThen(RxFirebaseFirestore.delete(provideProperEventsCollection(uid).document(id)))
            .subscribeOn(Schedulers.io())
    }

    fun deleteReferenceEvent(
        uid: String,
        id: String,
        creator: String,
        participantsLimit: Int
    ): Completable {
        return RxFirebaseFirestore.delete(provideReferenceEventsCollection(uid).document(id))
            .andThen(
                RxFirebaseFirestore.update(
                    provideProperEventsCollection(creator).document(id),
                    mapOf(
                        Pair(EventDto::participants.name, arrayRemove(uid)),
                        Pair(EventDto::participantsLimit.name, participantsLimit - 1)
                    )
                )
            )
    }

    fun deleteReferenceRequestingEvent(
        id: String,
        creator: String,
        requesting: String
    ): Completable {
        return RxFirebaseFirestore.delete(provideReferenceEventsCollection(requesting).document(id))
            .andThen(
                RxFirebaseFirestore.update(
                    provideProperEventsCollection(creator).document(id),
                    mapOf(Pair(EventDto::requesting.name, arrayRemove(requesting)))
                )
            )
    }

    fun deleteReferencePendingEvent(
        id: String,
        creator: String,
        pending: String,
        participantsLimit: Int
    ): Completable {
        return RxFirebaseFirestore.delete(provideReferenceEventsCollection(pending).document(id))
            .andThen(
                RxFirebaseFirestore.update(
                    provideProperEventsCollection(creator).document(id),
                    mapOf(
                        Pair(EventDto::pending.name, arrayRemove(pending)),
                        Pair(EventDto::participantsLimit.name, participantsLimit - 1)
                    )
                )
            )
    }

    fun addEvent(
        uid: String,
        title: String,
        description: String,
        pending: List<String>,
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
        repeatMode: String,
        locationAddress: String?,
        locationLatitude: Double?,
        locationLongitude: Double?
    ): Completable {
        return provideProperEventsCollection(uid).document().id.run {
            Observable.fromIterable(pending).flatMapCompletable {
                addReferencePendingEvent(
                    uid = it,
                    id = this,
                    creator = uid,
                    weekDay = weekDay,
                    monthDay = monthDay,
                    month = month,
                    year = year,
                    repeatMode = repeatMode
                )
            }.andThen(
                addProperEvent(
                    uid = uid,
                    id = this,
                    title = title,
                    description = description,
                    pending = pending,
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
                    repeatMode = repeatMode,
                    locationAddress = locationAddress,
                    locationLatitude = locationLatitude,
                    locationLongitude = locationLongitude
                )
            ).subscribeOn(Schedulers.io())
        }
    }

    fun addReferenceFromRequestingActiveEvent(
        id: String,
        creator: String,
        requesting: String,
        participants: List<String>
    ): Completable {
        return RxFirebaseFirestore.update(
            provideReferenceEventsCollection(requesting).document(id),
            mapOf(Pair(EventReferenceDto::type.name, ACTIVE.name))
        ).andThen(
            RxFirebaseFirestore.update(
                provideProperEventsCollection(creator).document(id),
                mapOf(Pair(EventDto::requesting.name, arrayRemove(requesting)))
            )
        ).andThen(
            RxFirebaseFirestore.update(
                provideProperEventsCollection(creator).document(id),
                mapOf(Pair(EventDto::participants.name, participants.append(requesting).distinct()))
            )
        )
    }

    fun addReferenceFromPendingActiveEvent(
        id: String,
        creator: String,
        pending: String,
        participants: List<String>
    ): Completable {
        return RxFirebaseFirestore.update(
            provideReferenceEventsCollection(pending).document(id),
            mapOf(Pair(EventReferenceDto::type.name, ACTIVE.name))
        ).andThen(
            RxFirebaseFirestore.update(
                provideProperEventsCollection(creator).document(id),
                mapOf(Pair(EventDto::pending.name, arrayRemove(pending)))
            )
        ).andThen(
            RxFirebaseFirestore.update(
                provideProperEventsCollection(creator).document(id),
                mapOf(Pair(EventDto::participants.name, participants.append(pending).distinct()))
            )
        )
    }

    fun addReferenceRequestingEvent(
        uid: String,
        id: String,
        creator: String,
        requesting: List<String>,
        weekDay: Int = 0,
        monthDay: Int = 0,
        month: Int = 0,
        year: Int = 0,
        repeatMode: String
    ): Completable {
        return addReferenceEvent(
            uid = uid,
            id = id,
            creator = creator,
            type = REQUESTING.name,
            weekDay = weekDay,
            monthDay = monthDay,
            month = month,
            year = year,
            repeatMode = repeatMode
        ).andThen(
            RxFirebaseFirestore.update(
                provideProperEventsCollection(creator).document(id),
                mapOf(Pair(EventDto::requesting.name, requesting.append(uid).distinct()))
            )
        ).subscribeOn(Schedulers.io())
    }

    private fun addReferencePendingEvent(
        uid: String,
        id: String,
        creator: String,
        weekDay: Int = 0,
        monthDay: Int = 0,
        month: Int = 0,
        year: Int = 0,
        repeatMode: String
    ): Completable {
        return addReferenceEvent(
            uid = uid,
            id = id,
            creator = creator,
            type = PENDING.name,
            weekDay = weekDay,
            monthDay = monthDay,
            month = month,
            year = year,
            repeatMode = repeatMode
        ).subscribeOn(Schedulers.io())
    }

    private fun addReferenceEvent(
        uid: String,
        id: String,
        creator: String,
        type: String,
        weekDay: Int = 0,
        monthDay: Int = 0,
        month: Int = 0,
        year: Int = 0,
        repeatMode: String
    ): Completable {
        return RxFirebaseFirestore.set(
            provideReferenceEventsCollection(uid).document(id),
            EventReferenceDto(
                type = type,
                weekDay = weekDay,
                monthDay = monthDay,
                month = month,
                year = year,
                repeatMode = repeatMode,
                reference = provideProperEventsCollection(creator).document(id)
            )
        ).subscribeOn(Schedulers.io())
    }

    private fun addProperEvent(
        uid: String,
        id: String,
        title: String,
        description: String,
        pending: List<String>,
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
        repeatMode: String,
        locationAddress: String?,
        locationLatitude: Double?,
        locationLongitude: Double?
    ): Completable {
        return RxFirebaseFirestore.set(
            provideProperEventsCollection(uid).document(id),
            EventDto(
                title = title,
                description = description,
                creatorId = uid,
                pending = pending,
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
                repeatMode = repeatMode,
                locationAddress = locationAddress,
                locationLatitude = locationLatitude,
                locationLongitude = locationLongitude
            )
        ).subscribeOn(Schedulers.io())
    }
}
