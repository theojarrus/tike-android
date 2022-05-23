package com.theost.tike.data.repositories

import com.androidhuman.rxfirebase2.firestore.RxFirebaseFirestore
import com.theost.tike.data.api.FirestoreApi.provideProperEventsCollection
import com.theost.tike.data.api.FirestoreApi.provideReferenceEventsCollection
import com.theost.tike.data.models.core.Dates
import com.theost.tike.data.models.dto.EventDto
import com.theost.tike.data.models.dto.EventReferenceDto
import com.theost.tike.data.models.state.ReferenceType.ACTIVE
import com.theost.tike.data.models.state.RepeatMode.*
import com.theost.tike.ui.extensions.getOrNull
import com.theost.tike.ui.extensions.isFalse
import com.theost.tike.ui.extensions.toLongInt
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

object DatesRepository {

    fun observeEventsDates(uid: String): Observable<Dates> {
        return Observable.combineLatest(
            observeProperEventsDates(uid),
            observeReferenceEventsDates(uid)
        ) { properDates, referenceDates ->
            if (properDates.hasDailyEvent || referenceDates.hasDailyEvent) {
                Dates(hasDailyEvent = true)
            } else {
                Dates(
                    days = properDates.days + referenceDates.days,
                    yearDays = properDates.yearDays + referenceDates.yearDays,
                    monthDays = properDates.monthDays + referenceDates.monthDays,
                    weekDays = properDates.weekDays + referenceDates.weekDays
                )
            }
        }
    }

    private fun observeReferenceEventsDates(uid: String): Observable<Dates> {
        return observeReferenceRepeatedDayEventsDates(uid).switchMap { hasDailyEvent ->
            if (!hasDailyEvent) {
                Observable.combineLatest(
                    observeReferenceDateEventsDates(uid),
                    observeReferenceRepeatedYearEventsDates(uid),
                    observeReferenceRepeatedMonthEventsDates(uid),
                    observeReferenceRepeatedWeekEventsDates(uid)
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

    private fun observeReferenceDateEventsDates(uid: String): Observable<List<Triple<Int, Int, Int>>> {
        return RxFirebaseFirestore.dataChanges(
            provideReferenceEventsCollection(uid)
                .whereEqualTo(EventReferenceDto::type.name, ACTIVE)
                .whereEqualTo(EventReferenceDto::repeatMode.name, NEVER)
        ).map { value ->
            value.getOrNull()?.map {
                it.data.run {
                    Triple(
                        get(EventReferenceDto::year.name).toLongInt(),
                        get(EventReferenceDto::month.name).toLongInt(),
                        get(EventReferenceDto::monthDay.name).toLongInt()
                    )
                }
            } ?: emptyList()
        }.subscribeOn(Schedulers.io())
    }

    private fun observeReferenceRepeatedYearEventsDates(
        uid: String
    ): Observable<List<Pair<Int, Int>>> {
        return RxFirebaseFirestore.dataChanges(
            provideReferenceEventsCollection(uid)
                .whereEqualTo(EventReferenceDto::type.name, ACTIVE)
                .whereEqualTo(EventReferenceDto::repeatMode.name, YEAR.name)
        ).map { value ->
            value.getOrNull()?.map {
                it.data.run {
                    Pair(
                        get(EventReferenceDto::month.name).toLongInt(),
                        get(EventReferenceDto::monthDay.name).toLongInt()
                    )
                }
            } ?: emptyList()
        }.subscribeOn(Schedulers.io())
    }

    private fun observeReferenceRepeatedMonthEventsDates(uid: String): Observable<List<Int>> {
        return RxFirebaseFirestore.dataChanges(
            provideReferenceEventsCollection(uid)
                .whereEqualTo(EventReferenceDto::repeatMode.name, MONTH.name)
        ).map { value ->
            value.getOrNull()?.map {
                it.get(EventReferenceDto::monthDay.name).toLongInt()
            } ?: emptyList()
        }.subscribeOn(Schedulers.io())
    }

    private fun observeReferenceRepeatedWeekEventsDates(uid: String): Observable<List<Int>> {
        return RxFirebaseFirestore.dataChanges(
            provideReferenceEventsCollection(uid)
                .whereEqualTo(EventReferenceDto::type.name, ACTIVE)
                .whereEqualTo(EventReferenceDto::repeatMode.name, WEEK.name)
        ).map { value ->
            value.getOrNull()?.map { it.get(EventDto::weekDay.name).toLongInt() } ?: emptyList()
        }.subscribeOn(Schedulers.io())
    }

    private fun observeReferenceRepeatedDayEventsDates(uid: String): Observable<Boolean> {
        return RxFirebaseFirestore.dataChanges(
            provideReferenceEventsCollection(uid)
                .whereEqualTo(EventReferenceDto::type.name, ACTIVE)
                .whereEqualTo(EventReferenceDto::repeatMode.name, DAY.name)
        ).map { it.getOrNull()?.isEmpty.isFalse() }
            .subscribeOn(Schedulers.io())
    }

    private fun observeProperEventsDates(uid: String): Observable<Dates> {
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
            value.getOrNull()?.map {
                it.data.run {
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
            value.getOrNull()?.map {
                it.data.run {
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
        ).map { value ->
            value.getOrNull()?.map { it.get(EventDto::monthDay.name).toLongInt() } ?: emptyList()
        }.subscribeOn(Schedulers.io())
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
}
