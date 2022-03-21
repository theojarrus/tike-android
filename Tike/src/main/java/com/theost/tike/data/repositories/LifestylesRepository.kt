package com.theost.tike.data.repositories

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.theost.tike.data.models.core.Lifestyle
import com.theost.tike.data.models.dto.LifestyleDto
import com.theost.tike.data.models.dto.mapToLifestyle
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object LifestylesRepository {

    private const val FIREBASE_COLLECTION_LIFESTYLES = "lifestyles"

    private const val FIREBASE_DOCUMENT_LIFESTYLE_NAME = "name"
    private const val FIREBASE_DOCUMENT_LIFESTYLE_ICON = "icon"
    private const val FIREBASE_DOCUMENT_LIFESTYLE_TEXT = "text"
    private const val FIREBASE_DOCUMENT_LIFESTYLE_EXTRA = "extra"

    fun getLifestyle(id: String): Single<Lifestyle> {
        return Single.create<DocumentSnapshot> { emitter ->
            FirebaseFirestore.getInstance().collection(FIREBASE_COLLECTION_LIFESTYLES)
                .document(id)
                .get()
                .addOnSuccessListener { snapshot -> emitter.onSuccess(snapshot) }
                .addOnFailureListener { error -> emitter.onError(error) }
        }.map { snapshot ->
            snapshot.toObject(LifestyleDto::class.java)
        }.map { entity ->
            entity.mapToLifestyle()
        }.subscribeOn(Schedulers.io())
    }

    fun getLifestyles(ids: List<String>): Single<List<Lifestyle>> {
        return if (ids.isNotEmpty()) {
            Single.create<QuerySnapshot> { emitter ->
                FirebaseFirestore.getInstance().collection(FIREBASE_COLLECTION_LIFESTYLES)
                    .whereIn(FieldPath.documentId(), ids)
                    .get()
                    .addOnSuccessListener { snapshot -> emitter.onSuccess(snapshot) }
                    .addOnFailureListener { error -> emitter.onError(error) }
            }.map { snapshot ->
                snapshot.toObjects(LifestyleDto::class.java)
            }.map { entities ->
                entities.map { entity -> entity.mapToLifestyle() }.sortedBy { it.name }
            }.subscribeOn(Schedulers.io())
        } else Single.just(emptyList())
    }

    fun getLifestyles(): Single<List<Lifestyle>> {
        return Single.create<QuerySnapshot> { emitter ->
            FirebaseFirestore.getInstance().collection(FIREBASE_COLLECTION_LIFESTYLES)
                .get()
                .addOnSuccessListener { snapshot -> emitter.onSuccess(snapshot) }
                .addOnFailureListener { error -> emitter.onError(error) }
        }.map { snapshot ->
            snapshot.toObjects(LifestyleDto::class.java)
        }.map { lifestyles ->
            lifestyles.map { entity -> entity.mapToLifestyle() }.sortedBy { it.name }
        }.subscribeOn(Schedulers.io())
    }

}