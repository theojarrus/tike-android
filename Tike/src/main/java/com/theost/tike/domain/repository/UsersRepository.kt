package com.theost.tike.domain.repository

import com.androidhuman.rxfirebase2.firestore.RxFirebaseFirestore
import com.google.firebase.firestore.FieldPath.documentId
import com.theost.tike.common.exception.ExistException
import com.theost.tike.common.extension.getOrNull
import com.theost.tike.domain.api.FirestoreApi.provideUserDocument
import com.theost.tike.domain.api.FirestoreApi.provideUsersCollection
import com.theost.tike.domain.model.core.User
import com.theost.tike.domain.model.dto.UserDto
import com.theost.tike.domain.model.dto.mapToUser
import com.theost.tike.domain.model.multi.ExistStatus
import com.theost.tike.domain.model.multi.ExistStatus.Exist
import com.theost.tike.domain.model.multi.ExistStatus.NotFound
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io

object UsersRepository {

    fun observeUsers(ids: List<String>): Observable<List<User>> {
        return if (ids.isNotEmpty()) {
            RxFirebaseFirestore.dataChanges(provideUsersCollection().whereIn(documentId(), ids))
                .map { it.getOrNull()?.toObjects(UserDto::class.java) ?: emptyList() }
                .map { entities -> entities.map { entity -> entity.mapToUser() } }
                .subscribeOn(io())
        } else {
            Observable.just(emptyList())
        }
    }

    fun getUsers(ids: List<String>): Single<List<User>> {
        return if (ids.isNotEmpty()) {
            RxFirebaseFirestore.data(provideUsersCollection().whereIn(documentId(), ids))
                .map { it.getOrNull()?.toObjects(UserDto::class.java) ?: emptyList() }
                .map { entities -> entities.map { entity -> entity.mapToUser() } }
                .subscribeOn(io())
        } else {
            Single.just(emptyList())
        }
    }

    fun observeAllUsers(excluded: List<String> = emptyList()): Observable<List<User>> {
        return if (excluded.isNotEmpty()) {
            RxFirebaseFirestore.dataChanges(
                provideUsersCollection().whereNotIn(documentId(), excluded)
            ).map { it.getOrNull()?.toObjects(UserDto::class.java) ?: emptyList() }
                .map { entities -> entities.map { entity -> entity.mapToUser() } }
                .subscribeOn(io())
        } else {
            RxFirebaseFirestore.dataChanges(provideUsersCollection())
                .map { it.getOrNull()?.toObjects(UserDto::class.java) ?: emptyList() }
                .map { entities -> entities.map { entity -> entity.mapToUser() } }
                .subscribeOn(io())
        }
    }

    private fun getUserNickStatus(nick: String): Single<ExistStatus> {
        return RxFirebaseFirestore.data(
            provideUsersCollection().whereEqualTo(
                UserDto::nick.name,
                nick
            )
        ).map { if (it.getOrNull()?.isEmpty == false) Exist else NotFound }
            .subscribeOn(io())
    }

    fun observeUser(uid: String): Observable<User> {
        return RxFirebaseFirestore.dataChanges(provideUserDocument(uid))
            .map { it.value().toObject(UserDto::class.java) }
            .map { entity -> entity.mapToUser() }
            .subscribeOn(io())
    }

    fun getUser(uid: String): Single<User> {
        return RxFirebaseFirestore.data(provideUserDocument(uid))
            .map { it.value().toObject(UserDto::class.java) }
            .map { entity -> entity.mapToUser() }
            .subscribeOn(io())
    }

    fun deleteUser(uid: String): Completable {
        return RxFirebaseFirestore.delete(provideUsersCollection().document(uid))
            .subscribeOn(io())
    }

    fun addUser(userDto: UserDto): Completable {
        return getUserNickStatus(userDto.nick).flatMapCompletable { status ->
            when (status) {
                Exist -> Completable.error(ExistException())
                NotFound -> RxFirebaseFirestore.set(
                    provideUsersCollection().document(userDto.uid),
                    userDto
                )
            }
        }.subscribeOn(io())
    }

    fun addUser(
        uid: String,
        name: String,
        nick: String,
        email: String,
        phone: String,
        avatar: String,
        lifestyles: List<String>
    ): Completable {
        return getUserNickStatus(nick).flatMapCompletable { status ->
            when (status) {
                Exist -> Completable.error(ExistException())
                NotFound -> RxFirebaseFirestore.set(
                    provideUsersCollection().document(uid),
                    UserDto(
                        name = name,
                        nick = nick,
                        email = email,
                        phone = phone,
                        avatar = avatar,
                        lifestyles = lifestyles
                    )
                )
            }
        }.subscribeOn(io())
    }
}
