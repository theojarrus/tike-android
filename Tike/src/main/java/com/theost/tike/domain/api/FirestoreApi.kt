package com.theost.tike.domain.api

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirestoreApi {

    const val SERVER_CLIENT_ID =
        "694580831967-k64snkpmp1k6bb38bps6fifaoh7acq6e.apps.googleusercontent.com"

    private const val FIREBASE_CONNECTION_REFERENCE = ".info/connected"

    private const val FIREBASE_DOCUMENT_COLLECTIONS = "collections"

    private const val FIREBASE_COLLECTION_CONFIGS = "configs"
    private const val FIREBASE_DOCUMENT_VERSIONS = "versions"
    private const val FIREBASE_DOCUMENT_TOGGLES = "toggles"

    private const val FIREBASE_COLLECTION_USERS = "users"
    private const val FIREBASE_COLLECTION_LIFESTYLES = "lifestyles"

    private const val FIREBASE_COLLECTION_EVENTS = "events"
    private const val FIREBASE_COLLECTION_EVENTS_PROPER = "proper"
    private const val FIREBASE_COLLECTION_EVENTS_REFERENCE = "reference"

    fun provideConnectionReference(): DatabaseReference {
        return Firebase.database.getReference(FIREBASE_CONNECTION_REFERENCE)
    }

    private fun provideConfigsCollection(): CollectionReference {
        return Firebase.firestore.collection(FIREBASE_COLLECTION_CONFIGS)
    }

    fun provideVersionsDocument(): DocumentReference {
        return provideConfigsCollection().document(FIREBASE_DOCUMENT_VERSIONS)
    }

    fun provideTogglesCollection(): CollectionReference {
        return provideConfigsCollection()
            .document(FIREBASE_DOCUMENT_TOGGLES)
            .collection(FIREBASE_DOCUMENT_COLLECTIONS)
    }

    fun provideUsersCollection(): CollectionReference {
        return Firebase.firestore.collection(FIREBASE_COLLECTION_USERS)
    }

    fun provideLifestylesCollection(): CollectionReference {
        return Firebase.firestore.collection(FIREBASE_COLLECTION_LIFESTYLES)
    }

    fun provideProperEventsCollection(uid: String): CollectionReference {
        return provideEventsDocument(uid).collection(FIREBASE_COLLECTION_EVENTS_PROPER)
    }

    fun provideReferenceEventsCollection(uid: String): CollectionReference {
        return provideEventsDocument(uid)
            .collection(FIREBASE_COLLECTION_EVENTS_REFERENCE)
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
