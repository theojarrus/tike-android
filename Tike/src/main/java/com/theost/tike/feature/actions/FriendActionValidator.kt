package com.theost.tike.feature.actions

import com.theost.tike.common.exception.ValidationException
import com.theost.tike.domain.model.core.User
import com.theost.tike.domain.model.multi.Direction.In
import com.theost.tike.domain.model.multi.Direction.Out
import com.theost.tike.domain.model.multi.FriendAction
import com.theost.tike.domain.model.multi.FriendAction.*
import io.reactivex.Single

class FriendActionValidator {

    operator fun invoke(
        friendAction: FriendAction,
        actionUser: User,
        actualUser: User
    ): Single<User> {
        val validation = when (friendAction) {
            is Accept -> {
                actionUser.requesting.contains(actualUser.uid)
                        && actualUser.pending.contains(actionUser.uid)
                        && !actualUser.friends.contains(actionUser.uid)
                        && !actionUser.friends.contains(actualUser.uid)
                        && !actualUser.blocked.contains(actionUser.uid)
                        && !actionUser.blocked.contains(actualUser.uid)
                        && !actualUser.requesting.contains(actionUser.uid)
                        && !actionUser.pending.contains(actualUser.uid)
            }
            is Add -> {
                !actualUser.friends.contains(actionUser.uid)
                        && !actionUser.friends.contains(actualUser.uid)
                        && !actualUser.blocked.contains(actionUser.uid)
                        && !actionUser.blocked.contains(actualUser.uid)
                        && !actualUser.requesting.contains(actionUser.uid)
                        && !actionUser.requesting.contains(actualUser.uid)
                        && !actualUser.pending.contains(actionUser.uid)
                        && !actionUser.pending.contains(actualUser.uid)
            }
            is Block -> {
                !actualUser.blocked.contains(actionUser.uid)
            }
            is Decline -> validateDeclineAction(friendAction, actionUser, actualUser)
            is Delete -> {
                actualUser.friends.contains(actionUser.uid)
                        && actionUser.friends.contains(actualUser.uid)
                        && !actualUser.blocked.contains(actionUser.uid)
                        && !actionUser.blocked.contains(actualUser.uid)
                        && !actualUser.requesting.contains(actionUser.uid)
                        && !actionUser.requesting.contains(actualUser.uid)
                        && !actualUser.pending.contains(actionUser.uid)
                        && !actionUser.pending.contains(actualUser.uid)
            }
            is Unblock -> {
                actualUser.blocked.contains(actionUser.uid)
            }
            else -> false
        }
        return if (validation) Single.just(actionUser) else Single.error(ValidationException())
    }

    private fun validateDeclineAction(
        friendAction: Decline,
        actionUser: User,
        actualUser: User
    ): Boolean {
        return when (friendAction.direction) {
            In -> {
                actionUser.requesting.contains(actualUser.uid)
                        && actualUser.pending.contains(actionUser.uid)
                        && !actionUser.blocked.contains(actualUser.uid)
                        && !actualUser.blocked.contains(actionUser.uid)
                        && !actualUser.friends.contains(actionUser.uid)
                        && !actionUser.friends.contains(actualUser.uid)
                        && !actualUser.requesting.contains(actionUser.uid)
                        && !actionUser.pending.contains(actualUser.uid)
            }
            Out -> {
                actualUser.requesting.contains(actionUser.uid)
                        && actionUser.pending.contains(actualUser.uid)
                        && !actionUser.blocked.contains(actualUser.uid)
                        && !actualUser.blocked.contains(actionUser.uid)
                        && !actualUser.friends.contains(actionUser.uid)
                        && !actionUser.friends.contains(actualUser.uid)
                        && !actionUser.requesting.contains(actualUser.uid)
                        && !actualUser.pending.contains(actionUser.uid)
            }
        }
    }
}
