package com.theost.tike.data.api

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirestoreApi {

    private const val FIREBASE_DOCUMENT_COLLECTIONS = "collections"

    private const val FIREBASE_COLLECTION_USERS = "users"
    private const val FIREBASE_COLLECTION_LIFESTYLES = "lifestyles"

    private const val FIREBASE_COLLECTION_EVENTS = "events"
    private const val FIREBASE_COLLECTION_EVENTS_PROPER = "proper"
    private const val FIREBASE_COLLECTION_EVENTS_REFERENCE = "reference"
    private const val FIREBASE_COLLECTION_EVENTS_REFERENCE_ACTIVE = "active"
    private const val FIREBASE_COLLECTION_EVENTS_REFERENCE_PENDING = "pending"

    fun provideUsersCollection(): CollectionReference {
        return Firebase.firestore.collection(FIREBASE_COLLECTION_USERS)
    }

    fun provideLifestylesCollection(): CollectionReference {
        return Firebase.firestore.collection(FIREBASE_COLLECTION_LIFESTYLES)
    }

    fun provideProperEventsCollection(uid: String): CollectionReference {
        return provideEventsDocument(uid).collection(FIREBASE_COLLECTION_EVENTS_PROPER)
    }

    fun provideActiveReferenceEventsCollection(uid: String): CollectionReference {
        return provideReferenceEventsDocument(uid)
            .collection(FIREBASE_COLLECTION_EVENTS_REFERENCE_ACTIVE)
    }

    fun providePendingReferenceEventsCollection(uid: String): CollectionReference {
        return provideReferenceEventsDocument(uid)
            .collection(FIREBASE_COLLECTION_EVENTS_REFERENCE_PENDING)
    }

    private fun provideReferenceEventsDocument(uid: String): DocumentReference {
        return provideEventsDocument(uid)
            .collection(FIREBASE_COLLECTION_EVENTS_REFERENCE)
            .document(FIREBASE_COLLECTION_EVENTS)
    }

    fun provideLifestyleDocument(id: String): DocumentReference {
        return provideLifestylesCollection().document(id)
    }

    fun provideUserDocument(uid: String): DocumentReference {
        return provideUsersCollection().document(uid)
    }

    private fun provideEventsDocument(uid: String): DocumentReference {
        return Firebase.firestore
            .collection(FIREBASE_COLLECTION_USERS)
            .document(uid)
            .collection(FIREBASE_COLLECTION_EVENTS)
            .document(FIREBASE_DOCUMENT_COLLECTIONS)
    }
}