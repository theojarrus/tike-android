package com.theost.tike.feature.actions

import com.theost.tike.common.exception.UnsupportedException
import com.theost.tike.domain.model.multi.Direction.In
import com.theost.tike.domain.model.multi.Direction.Out
import com.theost.tike.domain.model.multi.FriendAction
import com.theost.tike.domain.model.multi.FriendAction.*
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.FriendsRepository
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.Completable

class UpdateFriendInteractor(
    private val authRepository: AuthRepository,
    private val usersRepository: UsersRepository,
    private val friendsRepository: FriendsRepository,
    private val friendActionValidator: FriendActionValidator
) {

    operator fun invoke(friendAction: FriendAction): Completable {
        return authRepository.getActualUser()
            .flatMap { firebaseUser -> usersRepository.getRemoteUser(firebaseUser.uid) }
            .flatMapCompletable { actualUser ->
                usersRepository.getRemoteUser(friendAction.uid)
                    .flatMap { actionUser ->
                        friendActionValidator(
                            friendAction,
                            actionUser,
                            actualUser
                        )
                    }
                    .flatMapCompletable { actionUser ->
                        when (friendAction) {
                            is Accept -> friendsRepository.addFriend(
                                requesting = actualUser.uid,
                                requested = actionUser.uid,
                                requestingFriends = actualUser.friends,
                                requestedFriends = actionUser.friends
                            )
                            is Add -> friendsRepository.addFriendRequest(
                                requesting = actualUser.uid,
                                requested = actionUser.uid,
                                requestingRequesting = actualUser.requesting,
                                requestedPending = actionUser.pending
                            )
                            is Block -> friendsRepository.blockUser(
                                requesting = actualUser.uid,
                                requested = actionUser.uid,
                                requestingBlocked = actualUser.blocked
                            )
                            is Decline -> getDeclineFriendCompletable(
                                friendAction = friendAction,
                                auid = actualUser.uid
                            )
                            is Delete -> friendsRepository.deleteFriend(
                                requesting = actualUser.uid,
                                requested = actionUser.uid,
                                requestingPending = actualUser.pending,
                                requestedRequesting = actionUser.requesting
                            )
                            is Unblock -> friendsRepository.unblockUser(
                                requesting = actualUser.uid,
                                requested = actionUser.uid
                            )
                            else -> Completable.error(UnsupportedException())
                        }
                    }
            }
    }

    private fun getDeclineFriendCompletable(
        friendAction: Decline,
        auid: String
    ): Completable {
        return when (friendAction.direction) {
            In -> friendsRepository.deleteFriendRequest(
                requesting = friendAction.uid,
                requested = auid
            )
            Out -> friendsRepository.deleteFriendRequest(
                requesting = auid,
                requested = friendAction.uid
            )
        }
    }
}
