package com.theost.tike.data.repositories

import com.androidhuman.rxfirebase2.firestore.RxFirebaseFirestore
import com.google.firebase.firestore.FieldPath.documentId
import com.theost.tike.data.api.FirestoreApi.provideUserDocument
import com.theost.tike.data.api.FirestoreApi.provideUsersCollection
import com.theost.tike.data.models.core.User
import com.theost.tike.data.models.dto.UserDto
import com.theost.tike.data.models.dto.mapToUser
import com.theost.tike.data.models.state.ExistStatus
import com.theost.tike.data.models.state.ExistStatus.Exist
import com.theost.tike.data.models.state.ExistStatus.NotFound
import com.theost.tike.ui.extensions.getOrNull
import com.theost.tike.ui.extensions.isNotEmptyNotEquals
import com.theost.tike.ui.widgets.ExistException
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object UsersRepository {

    fun getUsers(ids: List<String>, excludedIds: List<String> = emptyList()): Single<List<User>> {
        return when {
            ids.isNotEmptyNotEquals(excludedIds) -> getUsersWithFilter(ids, excludedIds)
            ids.isNotEmpty() -> getUsersWithoutFilter(ids)
            else -> Single.just(emptyList())
        }
    }

    private fun getUsersWithoutFilter(ids: List<String>): Single<List<User>> {
        return RxFirebaseFirestore.data(provideUsersCollection().whereIn(documentId(), ids))
            .map { snapshot -> snapshot.getOrNull()?.toObjects(UserDto::class.java) ?: emptyList() }
            .map { entities -> entities.map { entity -> entity.mapToUser() } }
            .subscribeOn(Schedulers.io())
    }

    private fun getUsersWithFilter(ids: List<String>, excluded: List<String>): Single<List<User>> {
        return RxFirebaseFirestore.data(provideUsersCollection().whereIn(documentId(), ids))
            .map { snapshot -> snapshot.getOrNull()?.toObjects(UserDto::class.java) ?: emptyList() }
            .map { entities ->
                entities.filter { entity -> !excluded.contains(entity.uid) }
                    .map { entity -> entity.mapToUser() }
            }
            .subscribeOn(Schedulers.io())
    }

    fun getAllUsers(excluded: List<String> = emptyList()): Single<List<User>> {
        return when {
            excluded.isNotEmpty() -> getUsersAllWithFilter(excluded)
            else -> getAllUsersWithoutFilter()
        }
    }

    private fun getAllUsersWithoutFilter(): Single<List<User>> {
        return RxFirebaseFirestore.data(provideUsersCollection())
            .map { snapshot -> snapshot.getOrNull()?.toObjects(UserDto::class.java) ?: emptyList() }
            .map { entities -> entities.map { entity -> entity.mapToUser() } }
            .subscribeOn(Schedulers.io())
    }

    private fun getUsersAllWithFilter(excluded: List<String>): Single<List<User>> {
        return RxFirebaseFirestore.data(provideUsersCollection().whereNotIn(documentId(), excluded))
            .map { snapshot -> snapshot.getOrNull()?.toObjects(UserDto::class.java) ?: emptyList() }
            .map { entities -> entities.map { entity -> entity.mapToUser() } }
            .subscribeOn(Schedulers.io())
    }

    private fun getNicknameExist(nick: String): Single<ExistStatus> {
        return RxFirebaseFirestore.data(
            provideUsersCollection().whereEqualTo(
                UserDto::nick.name,
                nick
            )
        ).map { snapshot -> if (snapshot.getOrNull()?.isEmpty == false) Exist else NotFound }
            .subscribeOn(Schedulers.io())
    }

    fun getUser(uid: String): Single<User> {
        return RxFirebaseFirestore.data(provideUserDocument(uid))
            .map { it.value().toObject(UserDto::class.java) }
            .map { entity -> entity.mapToUser() }
            .subscribeOn(Schedulers.io())
    }

    fun deleteUser(uid: String): Completable {
        return RxFirebaseFirestore.delete(provideUsersCollection().document(uid))
            .subscribeOn(Schedulers.io())
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
        return getNicknameExist(nick).flatMapCompletable { status ->
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
        }.subscribeOn(Schedulers.io())
    }
}
