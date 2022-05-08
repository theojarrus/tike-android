package com.theost.tike.data.repositories

import com.androidhuman.rxfirebase2.firestore.RxFirebaseFirestore
import com.google.firebase.firestore.FieldPath.documentId
import com.theost.tike.data.api.FirestoreApi.provideLifestyleDocument
import com.theost.tike.data.api.FirestoreApi.provideLifestylesCollection
import com.theost.tike.data.models.core.Lifestyle
import com.theost.tike.data.models.dto.LifestyleDto
import com.theost.tike.data.models.dto.mapToLifestyle
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object LifestylesRepository {

    fun getLifestyle(id: String): Single<Lifestyle> {
        return RxFirebaseFirestore.data(provideLifestyleDocument(id))
            .map { snapshot -> snapshot.value().toObject(LifestyleDto::class.java) }
            .map { entity -> entity.mapToLifestyle() }
            .subscribeOn(Schedulers.io())
    }

    fun getLifestyles(): Single<List<Lifestyle>> {
        return RxFirebaseFirestore.data(provideLifestylesCollection())
            .map { snapshot -> snapshot.value().toObjects(LifestyleDto::class.java) }
            .map { entities -> entities.map { entity -> entity.mapToLifestyle() } }
            .subscribeOn(Schedulers.io())
    }

    fun getLifestyles(ids: List<String>): Single<List<Lifestyle>> {
        return if (ids.isNotEmpty()) {
            RxFirebaseFirestore.data(provideLifestylesCollection().whereIn(documentId(), ids))
                .map { snapshot -> snapshot.value().toObjects(LifestyleDto::class.java) }
                .map { entities -> entities.map { entity -> entity.mapToLifestyle() } }
                .subscribeOn(Schedulers.io())
        } else {
            Single.just(emptyList())
        }
    }
}