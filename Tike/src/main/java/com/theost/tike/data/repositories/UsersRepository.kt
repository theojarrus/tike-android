package com.theost.tike.data.repositories

import com.google.firebase.firestore.*
import com.theost.tike.data.models.core.User
import com.theost.tike.data.models.dto.UserDto
import com.theost.tike.data.models.dto.mapToUser
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

    fun getUser(userId: String): Single<User> {
        return Single.create<DocumentSnapshot> { emitter ->
            FirebaseFirestore.getInstance().collection(FIREBASE_COLLECTION_USERS)
                .document(userId)
                .get()
                .addOnSuccessListener { snapshot -> emitter.onSuccess(snapshot) }
                .addOnFailureListener { error -> emitter.onError(error) }
        }.map { snapshot ->
            snapshot.toObject(UserDto::class.java)
        }.map { user ->
            user.mapToUser()
        }.subscribeOn(Schedulers.io())
    }

    fun getUsers(userIds: List<String>, hiddenIds: List<String> = emptyList()): Single<List<User>> {
        return if (userIds.isNotEmpty()) {
            Single.create<QuerySnapshot> { emitter ->
                getUsersQuery(userIds, hiddenIds).get()
                    .addOnSuccessListener { snapshot -> emitter.onSuccess(snapshot) }
                    .addOnFailureListener { error -> emitter.onError(error) }
            }.map { snapshot ->
                snapshot.toObjects(UserDto::class.java)
            }.map { users ->
                users.map { entity -> entity.mapToUser() }.sortedBy { user -> user.firstName }
            }.subscribeOn(Schedulers.io())
        } else Single.just(emptyList())
    }

    private fun getUsersQuery(userIds: List<String>, hiddenIds: List<String>): Query {
        val query = FirebaseFirestore.getInstance().collection(FIREBASE_COLLECTION_USERS)
            .whereIn(FieldPath.documentId(), userIds)
        return if (hiddenIds.isNotEmpty()) query.whereNotIn(
            FieldPath.documentId(),
            hiddenIds
        ) else query
    }

}