package com.theost.tike.domain.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Completable

object CacheRepository {

    fun clearCache(): Completable {
        return Completable.create { emitter ->
            Firebase.firestore.terminate()
                .addOnFailureListener { emitter.tryOnError(it) }
                .addOnSuccessListener {
                    Firebase.firestore.clearPersistence()
                        .addOnFailureListener { emitter.tryOnError(it) }
                        .addOnSuccessListener { emitter.onComplete() }
                }
        }
    }
}
