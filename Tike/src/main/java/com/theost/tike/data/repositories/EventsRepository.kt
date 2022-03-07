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

    private const val FIREBASE_DOCUMENT_EVENT_TITLE = "title"
    private const val FIREBASE_DOCUMENT_EVENT_DESCRIPTION = "description"
    private const val FIREBASE_DOCUMENT_EVENT_CREATOR_ID = "creatorId"
    private const val FIREBASE_DOCUMENT_EVENT_PARTICIPANTS = "participants"
    private const val FIREBASE_DOCUMENT_EVENT_PARTICIPANTS_LIMIT = "participantsLimit"
    private const val FIREBASE_DOCUMENT_EVENT_DATE = "date"
    private const val FIREBASE_DOCUMENT_EVENT_BEGIN_TIME = "beginTime"
    private const val FIREBASE_DOCUMENT_EVENT_END_TIME = "endTime"
    private const val FIREBASE_DOCUMENT_EVENT_REPEAT_MODE = "repeatMode"

    fun getEvents(date: Long): Observable<List<Event>> {
        return Observable.create<QuerySnapshot> { emitter ->
            val queryListener = FirebaseFirestore.getInstance().collection(FIREBASE_COLLECTION_EVENTS)
                .whereEqualTo(FIREBASE_DOCUMENT_EVENT_DATE, date)
                .addSnapshotListener { snapshot, error ->
                    if (error == null) {
                        snapshot?.let { emitter.onNext(snapshot) }
                    } else {
                        emitter.onError(error)
                    }
                }
            emitter.setCancellable {
                queryListener.remove()
            }
        }.map { querySnapshot ->
            querySnapshot.toObjects(EventDto::class.java)
        }.map { events ->
            events.map { entity -> entity.mapToEvent() }.sortedBy { event -> event.beginTime }
        }.subscribeOn(Schedulers.io())
    }

    fun addEvents(
        title: String,
        description: String,
        participants: List<Int>,
        date: Long,
        beginTime: Long,
        endTime: Long,
        repeatMode: String
    ): Completable {
        return Completable.fromAction {
            FirebaseFirestore.getInstance().collection(FIREBASE_COLLECTION_EVENTS)
                .add(
                    hashMapOf(
                        FIREBASE_DOCUMENT_EVENT_TITLE to title,
                        FIREBASE_DOCUMENT_EVENT_DESCRIPTION to description,
                        FIREBASE_DOCUMENT_EVENT_CREATOR_ID to 0,
                        FIREBASE_DOCUMENT_EVENT_PARTICIPANTS to participants,
                        FIREBASE_DOCUMENT_EVENT_PARTICIPANTS_LIMIT to 0,
                        FIREBASE_DOCUMENT_EVENT_DATE to date,
                        FIREBASE_DOCUMENT_EVENT_BEGIN_TIME to beginTime,
                        FIREBASE_DOCUMENT_EVENT_END_TIME to endTime,
                        FIREBASE_DOCUMENT_EVENT_REPEAT_MODE to repeatMode
                    )
                )
        }.subscribeOn(Schedulers.io())
    }

}