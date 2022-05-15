package com.theost.tike.data.repositories

import com.androidhuman.rxfirebase2.firestore.RxFirebaseFirestore
import com.google.firebase.firestore.FieldValue.arrayRemove
import com.theost.tike.data.api.FirestoreApi.provideUsersCollection
import com.theost.tike.data.models.dto.UserDto
import com.theost.tike.ui.extensions.append
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

object PeopleRepository {

    fun addFriendRequest(
        requesting: String,
        requested: String,
        requestedPending: List<String>
    ): Completable {
        return RxFirebaseFirestore.update(
            provideUsersCollection().document(requested),
            mapOf(Pair(UserDto::pending.name, requestedPending.append(requesting).distinct()))
        ).subscribeOn(Schedulers.io())
    }

    fun deleteFriendRequest(requesting: String, requested: String): Completable {
        return RxFirebaseFirestore.update(
            provideUsersCollection().document(requested),
            mapOf(Pair(UserDto::pending.name, arrayRemove(requesting)))
        ).subscribeOn(Schedulers.io())
    }

    fun addFriend(
        requesting: String,
        requested: String,
        requestingFriends: List<String>,
        requestedFriends: List<String>
    ): Completable {
        return addSingleFriend(requesting, requested, requestingFriends)
            .andThen(addSingleFriend(requested, requesting, requestedFriends))
            .andThen(deleteFriendRequest(requested, requesting))
    }

    private fun addSingleFriend(
        requesting: String,
        requested: String,
        requestingFriends: List<String>
    ): Completable {
        return RxFirebaseFirestore.update(
            provideUsersCollection().document(requesting),
            mapOf(Pair(UserDto::friends.name, requestingFriends.append(requested).distinct()))
        ).subscribeOn(Schedulers.io())
    }

    fun deleteFriend(
        requesting: String,
        requested: String,
        requestingPending: List<String>
    ): Completable {
        return addFriendRequest(requested, requesting, requestingPending)
            .andThen(deleteSingleFriend(requested, requesting))
            .andThen(deleteSingleFriend(requesting, requested))
    }

    private fun deleteSingleFriend(requesting: String, requested: String): Completable {
        return RxFirebaseFirestore.update(
            provideUsersCollection().document(requesting),
            mapOf(Pair(UserDto::friends.name, arrayRemove(requested)))
        ).subscribeOn(Schedulers.io())
    }

    fun blockUser(
        requesting: String,
        requested: String,
        requestingBlocked: List<String>
    ): Completable {
        return deleteSingleFriend(requested, requesting)
            .andThen(deleteSingleFriend(requesting, requested))
            .andThen(deleteFriendRequest(requested, requesting))
            .andThen(deleteFriendRequest(requesting, requested))
            .andThen(
                RxFirebaseFirestore.update(
                    provideUsersCollection().document(requesting),
                    mapOf(
                        Pair(
                            UserDto::blocked.name,
                            requestingBlocked.append(requested).distinct()
                        )
                    ),
                )
            ).subscribeOn(Schedulers.io())
    }

    fun unblockUser(requesting: String, requested: String): Completable {
        return RxFirebaseFirestore.update(
            provideUsersCollection().document(requesting),
            mapOf(Pair(UserDto::blocked.name, arrayRemove(requested)))
        ).subscribeOn(Schedulers.io())
    }
}