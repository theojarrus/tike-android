package com.theost.tike.domain.repository

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io

object ConfigRepository {

    fun fetchConfig(): Single<Boolean> {
        return Single.create { emitter ->
            Firebase.remoteConfig.fetchAndActivate()
                .addOnFailureListener { emitter.tryOnError(it) }
                .addOnSuccessListener { emitter.onSuccess(it) }
        }.subscribeOn(io())
    }
}
