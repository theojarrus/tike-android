package com.theost.tike.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.theost.tike.data.models.core.Event
import com.theost.tike.data.models.dto.EventDto
import com.theost.tike.data.models.dto.mapToEvent
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

object EventsRepository {

    private const val FIREBASE_COLLECTION_EVENTS = "events"
    private const val FIREBASE_COLLECTION_EVENTS_COLLECTIONS = "collections"

    private const val FIREBASE_COLLECTION_EVENTS_PROPER = "proper"
    private const val FIREBASE_COLLECTION_EVENTS_REFERENCE = "reference"

    private const val FIREBASE_COLLECTION_EVENTS_REFERENCE_ACTIVE = "active"
    private const val FIREBASE_COLLECTION_EVENTS_REFERENCE_PENDING = "pending"

    private const val FIREBASE_COLLECTION_EVENTS_REFERENCE_PENDING_CREATION = "creation"
    private const val FIREBASE_COLLECTION_EVENTS_REFERENCE_PENDING_DELETION = "creation"

    private const val FIREBASE_DOCUMENT_EVENT_TITLE = "title"
    private const val FIREBASE_DOCUMENT_EVENT_DESCRIPTION = "description"
    private const val FIREBASE_DOCUMENT_EVENT_CREATOR_ID = "creatorId"
    private const val FIREBASE_DOCUMENT_EVENT_PARTICIPANTS = "participants"
    private const val FIREBASE_DOCUMENT_EVENT_PARTICIPANTS_LIMIT = "participantsLimit"
    private const val FIREBASE_DOCUMENT_EVENT_CREATED = "created"
    private const val FIREBASE_DOCUMENT_EVENT_MODIFIED = "modified"
    private const val FIREBASE_DOCUMENT_EVENT_WEEK_DAY = "weekDay"
    private const val FIREBASE_DOCUMENT_EVENT_MONTH_DAY = "monthDay"
    private const val FIREBASE_DOCUMENT_EVENT_MONTH = "month"
    private const val FIREBASE_DOCUMENT_EVENT_YEAR = "year"
    private const val FIREBASE_DOCUMENT_EVENT_BEGIN_TIME = "beginTime"
    private const val FIREBASE_DOCUMENT_EVENT_END_TIME = "endTime"
    private const val FIREBASE_DOCUMENT_EVENT_REPEAT_MODE = "repeatMode"

    fun getEvents(year: Int, month: Int, day: Int): Observable<List<Event>> {
        return Observable.create<QuerySnapshot> { emitter ->
            val queryListener =
                FirebaseFirestore.getInstance().collection(FIREBASE_COLLECTION_EVENTS)
                    .document(FIREBASE_COLLECTION_EVENTS_COLLECTIONS)
                    .collection(FIREBASE_COLLECTION_EVENTS_PROPER)
                    .whereEqualTo(FIREBASE_DOCUMENT_EVENT_YEAR, year)
                    .whereEqualTo(FIREBASE_DOCUMENT_EVENT_MONTH, month)
                    .whereEqualTo(FIREBASE_DOCUMENT_EVENT_MONTH_DAY, day)
                    .addSnapshotListener { snapshot, error ->
                        if (error == null) {
                            snapshot?.let { emitter.onNext(snapshot) }
                        } else {
                            emitter.onError(error)
                        }
                    }
            emitter.setCancellable { queryListener.remove() }
        }.map { snapshot ->
            snapshot.toObjects(EventDto::class.java)
        }.map { events ->
            events.map { entity -> entity.mapToEvent() }
                .sortedBy { event -> event.endTime }
                .sortedBy { event -> event.beginTime }
        }.subscribeOn(Schedulers.io())
    }

    fun addEvent(
        title: String,
        description: String,
        participants: List<String>,
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
        return Completable.fromAction {
            FirebaseFirestore.getInstance().collection(FIREBASE_COLLECTION_EVENTS)
                .document(FIREBASE_COLLECTION_EVENTS_COLLECTIONS)
                .collection(FIREBASE_COLLECTION_EVENTS_PROPER)
                .add(
                    hashMapOf(
                        FIREBASE_DOCUMENT_EVENT_TITLE to title,
                        FIREBASE_DOCUMENT_EVENT_DESCRIPTION to description,
                        FIREBASE_DOCUMENT_EVENT_CREATOR_ID to 0,
                        FIREBASE_DOCUMENT_EVENT_PARTICIPANTS to participants,
                        FIREBASE_DOCUMENT_EVENT_PARTICIPANTS_LIMIT to 100,
                        FIREBASE_DOCUMENT_EVENT_CREATED to created,
                        FIREBASE_DOCUMENT_EVENT_MODIFIED to modified,
                        FIREBASE_DOCUMENT_EVENT_WEEK_DAY to weekDay,
                        FIREBASE_DOCUMENT_EVENT_MONTH_DAY to monthDay,
                        FIREBASE_DOCUMENT_EVENT_MONTH to month,
                        FIREBASE_DOCUMENT_EVENT_YEAR to year,
                        FIREBASE_DOCUMENT_EVENT_BEGIN_TIME to beginTime,
                        FIREBASE_DOCUMENT_EVENT_END_TIME to endTime,
                        FIREBASE_DOCUMENT_EVENT_REPEAT_MODE to repeatMode
                    )
                )
        }.subscribeOn(Schedulers.io())
    }

}