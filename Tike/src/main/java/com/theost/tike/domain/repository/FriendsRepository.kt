package com.theost.tike.domain.repository

import com.androidhuman.rxfirebase2.firestore.RxFirebaseFirestore
import com.google.firebase.firestore.FieldValue.arrayRemove
import com.theost.tike.common.extension.append
import com.theost.tike.domain.api.FirestoreApi.provideUsersCollection
import com.theost.tike.domain.model.dto.UserDto
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

object FriendsRepository {

    fun addFriendRequest(
        requesting: String,
        requested: String,
        requestingRequesting: List<String>,
        requestedPending: List<String>
    ): Completable {
        return RxFirebaseFirestore.update(
            provideUsersCollection().document(requested),
            mapOf(Pair(UserDto::pending.name, requestedPending.append(requesting).distinct()))
        ).andThen(
            RxFirebaseFirestore.update(
                provideUsersCollection().document(requesting),
                mapOf(
                    Pair(
                        UserDto::requesting.name,
                        requestingRequesting.append(requested).distinct()
                    )
                )
            )
        ).subscribeOn(Schedulers.io())
    }

    fun deleteFriendRequest(requesting: String, requested: String): Completable {
        return RxFirebaseFirestore.update(
            provideUsersCollection().document(requested),
            mapOf(Pair(UserDto::pending.name, arrayRemove(requesting)))
        ).andThen(
            RxFirebaseFirestore.update(
                provideUsersCollection().document(requesting),
                mapOf(Pair(UserDto::requesting.name, arrayRemove(requested)))
            )
        ).subscribeOn(Schedulers.io())
    }

    fun addFriend(
        requesting: String,
        requested: String,
        requestingFriends: List<String>,
        requestedFriends: List<String>
    ): Completable {
        return deleteFriendRequest(requested, requesting)
            .andThen(addSingleFriend(requesting, requested, requestingFriends))
            .andThen(addSingleFriend(requested, requesting, requestedFriends))
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
        requestingPending: List<String>,
        requestedRequesting: List<String>,
    ): Completable {
        return addFriendRequest(requested, requesting, requestedRequesting, requestingPending)
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
