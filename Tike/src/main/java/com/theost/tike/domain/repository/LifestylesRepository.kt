package com.theost.tike.domain.repository

import com.androidhuman.rxfirebase2.firestore.RxFirebaseFirestore
import com.google.firebase.firestore.FieldPath.documentId
import com.theost.tike.common.extension.getOrNull
import com.theost.tike.domain.api.FirestoreApi.provideLifestyleDocument
import com.theost.tike.domain.api.FirestoreApi.provideLifestylesCollection
import com.theost.tike.domain.model.core.Lifestyle
import com.theost.tike.domain.model.dto.LifestyleDto
import com.theost.tike.domain.model.dto.mapToLifestyle
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object LifestylesRepository {

    @Suppress("unused")
    fun getLifestyle(id: String): Single<Lifestyle> {
        return RxFirebaseFirestore.data(provideLifestyleDocument(id))
            .map { it.getOrNull()?.toObject(LifestyleDto::class.java) }
            .map { entity -> entity.mapToLifestyle() }
            .subscribeOn(Schedulers.io())
    }

    fun getLifestyles(): Single<List<Lifestyle>> {
        return RxFirebaseFirestore.data(provideLifestylesCollection())
            .map { it.getOrNull()?.toObjects(LifestyleDto::class.java) ?: emptyList() }
            .map { entities -> entities.map { entity -> entity.mapToLifestyle() } }
            .subscribeOn(Schedulers.io())
    }

    @Suppress("unused")
    fun getLifestyles(ids: List<String>): Single<List<Lifestyle>> {
        return if (ids.isNotEmpty()) {
            RxFirebaseFirestore.data(provideLifestylesCollection().whereIn(documentId(), ids))
                .map { it.getOrNull()?.toObjects(LifestyleDto::class.java) ?: emptyList() }
                .map { entities -> entities.map { entity -> entity.mapToLifestyle() } }
                .subscribeOn(Schedulers.io())
        } else {
            Single.just(emptyList())
        }
    }
}
