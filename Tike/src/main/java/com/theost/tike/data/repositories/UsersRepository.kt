package com.theost.tike.data.repositories

import com.google.firebase.firestore.*
import com.theost.tike.data.models.core.User
import com.theost.tike.data.models.dto.UserDto
import com.theost.tike.data.models.dto.mapToUser
import com.theost.tike.data.models.state.UserStatus
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object UsersRepository {

    private const val FIREBASE_COLLECTION_USERS = "users"

    private const val FIREBASE_DOCUMENT_USER_NAME = "name"
    private const val FIREBASE_DOCUMENT_USER_NICK = "nick"
    private const val FIREBASE_DOCUMENT_USER_EMAIL = "email"
    private const val FIREBASE_DOCUMENT_USER_AVATAR = "avatar"
    private const val FIREBASE_DOCUMENT_USER_LIFESTYLES = "lifestyles"
    private const val FIREBASE_DOCUMENT_USER_FRIENDS = "friends"
    private const val FIREBASE_DOCUMENT_USER_BLOCKED = "blocked"

    fun getUser(id: String): Single<User> {
        return Single.create<DocumentSnapshot> { emitter ->
            FirebaseFirestore.getInstance().collection(FIREBASE_COLLECTION_USERS)
                .document(id)
                .get()
                .addOnSuccessListener { snapshot -> emitter.onSuccess(snapshot) }
                .addOnFailureListener { error -> emitter.onError(error) }
        }.map { snapshot ->
            snapshot.toObject(UserDto::class.java)
        }.flatMap { entity ->
            LifestylesRepository.getLifestyles(entity.lifestyles).map { lifestyles ->
                entity.mapToUser(lifestyles)
            }
        }.subscribeOn(Schedulers.io())
    }

    fun getUsers(ids: List<String>, hiddenIds: List<String> = emptyList()): Single<List<User>> {
        return if (ids.isNotEmpty()) {
            Single.create<QuerySnapshot> { emitter ->
                getUsersQuery(ids, hiddenIds).get()
                    .addOnSuccessListener { snapshot -> emitter.onSuccess(snapshot) }
                    .addOnFailureListener { error -> emitter.onError(error) }
            }.map { snapshot ->
                snapshot.toObjects(UserDto::class.java)
            }.flatMap { entities ->
                Observable.fromIterable(entities).concatMapSingle { entity ->
                    LifestylesRepository.getLifestyles(entity.lifestyles).map { lifestyles ->
                        entity.mapToUser(lifestyles)
                    }
                }.toList()
            }.map { users ->
                users.sortedBy { user -> user.name }
            }.subscribeOn(Schedulers.io())
        } else Single.just(emptyList())
    }

    private fun getUsersQuery(ids: List<String>, hiddenIds: List<String>): Query {
        val query = FirebaseFirestore.getInstance().collection(FIREBASE_COLLECTION_USERS)
            .whereIn(FieldPath.documentId(), ids)
        return if (hiddenIds.isNotEmpty()) query.whereNotIn(
            FieldPath.documentId(),
            hiddenIds
        ) else query
    }

    fun getUserExist(userId: String): Observable<UserStatus> {
        return Observable.create<DocumentSnapshot> { emitter ->
            val queryListener =
                FirebaseFirestore.getInstance().collection(FIREBASE_COLLECTION_USERS)
                    .document(userId)
                    .addSnapshotListener { snapshot, error ->
                        if (error == null) {
                            snapshot?.let { emitter.onNext(snapshot) }
                        } else {
                            emitter.onError(error)
                        }
                    }
            emitter.setCancellable { queryListener.remove() }
        }.map { snapshot ->
            if (snapshot.exists()) UserStatus.Exist else UserStatus.NotFound
        }.subscribeOn(Schedulers.io())
    }

    fun getNickExist(nick: String): Single<UserStatus> {
        return Single.create<QuerySnapshot> { emitter ->
            FirebaseFirestore.getInstance().collection(FIREBASE_COLLECTION_USERS)
                .whereEqualTo(FIREBASE_DOCUMENT_USER_NICK, "@$nick")
                .get()
                .addOnSuccessListener { emitter.onSuccess(it) }
                .addOnFailureListener { emitter.onError(it) }
        }.map { snapshot ->
            if (snapshot.isEmpty) UserStatus.NotFound else UserStatus.Exist
        }.subscribeOn(Schedulers.io())
    }

    fun addUser(
        id: String,
        name: String,
        nick: String,
        email: String,
        phone: String,
        avatar: String,
        lifestyles: List<String>
    ): Completable {
        return Completable.fromAction {
            FirebaseFirestore.getInstance().collection(FIREBASE_COLLECTION_USERS)
                .document(id)
                .set(
                    UserDto(
                        id = id,
                        name = name,
                        nick = "@$nick",
                        email = email,
                        phone = phone,
                        avatar = avatar,
                        lifestyles = lifestyles,
                        friends = emptyList(),
                        blocked = emptyList()
                    )
                )
        }.subscribeOn(Schedulers.io())
    }

    fun deleteUser(userId: String): Completable {
        return Completable.fromAction {
            FirebaseFirestore.getInstance().collection(FIREBASE_COLLECTION_USERS)
                .document(userId)
                .delete()
        }.subscribeOn(Schedulers.io())
    }

}