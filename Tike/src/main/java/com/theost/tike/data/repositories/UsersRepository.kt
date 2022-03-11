package com.theost.tike.data.repositories

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.theost.tike.data.models.core.Event
import com.theost.tike.data.models.core.User
import com.theost.tike.data.models.dto.EventDto
import com.theost.tike.data.models.dto.UserDto
import com.theost.tike.data.models.dto.mapToEvent
import com.theost.tike.data.models.dto.mapToUser
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object UsersRepository {

    private const val FIREBASE_COLLECTION_USERS = "users"

    private const val FIREBASE_DOCUMENT_USER_FIRST_NAME = "firstName"
    private const val FIREBASE_DOCUMENT_USER_SECOND_NAME = "secondName"
    private const val FIREBASE_DOCUMENT_USER_NICK_NAME = "nickName"
    private const val FIREBASE_DOCUMENT_USER_EMAIL = "email"
    private const val FIREBASE_DOCUMENT_USER_PHONE = "phone"
    private const val FIREBASE_DOCUMENT_USER_AVATAR = "avatar"

    fun getUsers(userIds: List<String>): Single<List<User>> {
        return Single.create<QuerySnapshot> { emitter ->
            FirebaseFirestore.getInstance().collection(FIREBASE_COLLECTION_USERS)
                .whereIn(FieldPath.documentId(), userIds)
                .get()
                .addOnSuccessListener { snapshot -> emitter.onSuccess(snapshot) }
                .addOnFailureListener { error -> emitter.onError(error) }
        }.map { snapshot ->
            snapshot.toObjects(UserDto::class.java)
        }.map { users ->
            users.map { entity -> entity.mapToUser() }.sortedBy { user -> user.firstName }
        }.subscribeOn(Schedulers.io())
    }

    fun getUsers(userIds: List<String>, hiddenIds: List<String>): Single<List<User>> {
        return Single.create<QuerySnapshot> { emitter ->
            FirebaseFirestore.getInstance().collection(FIREBASE_COLLECTION_USERS)
                .whereNotIn(FieldPath.documentId(), hiddenIds)
                .whereIn(FieldPath.documentId(), userIds)
                .get()
                .addOnSuccessListener { snapshot -> emitter.onSuccess(snapshot) }
                .addOnFailureListener { error -> emitter.onError(error) }
        }.map { snapshot ->
            snapshot.toObjects(UserDto::class.java)
        }.map { users ->
            users.map { entity -> entity.mapToUser() }.sortedBy { user -> user.firstName }
        }.subscribeOn(Schedulers.io())
    }

}