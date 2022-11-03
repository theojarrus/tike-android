package com.theost.tike.domain.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers.io

object StoreRepository {

    fun enableRemoteStore(): Completable {
        return Completable.create { emitter ->
            Firebase.firestore.enableNetwork()
                .addOnFailureListener { emitter.tryOnError(it) }
                .addOnSuccessListener { emitter.onComplete() }
        }.subscribeOn(io())
    }

    fun disableRemoteStore(): Completable {
        return Completable.create { emitter ->
            Firebase.firestore.disableNetwork()
                .addOnFailureListener { emitter.tryOnError(it) }
                .addOnSuccessListener { emitter.onComplete() }
        }.subscribeOn(io())
    }
}
