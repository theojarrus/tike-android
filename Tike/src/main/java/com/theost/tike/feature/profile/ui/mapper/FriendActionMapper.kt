package com.theost.tike.feature.profile.ui.mapper

import com.theost.tike.domain.model.multi.Direction.Out
import com.theost.tike.domain.model.multi.FriendAction
import com.theost.tike.feature.profile.ui.model.ProfileAction
import com.theost.tike.feature.profile.ui.model.ProfileAction.*

class FriendActionMapper : (ProfileAction, String) -> FriendAction? {

    override fun invoke(profileAction: ProfileAction, uid: String): FriendAction? {
        return when (profileAction) {
            Accept -> FriendAction.Accept(uid)
            Add -> FriendAction.Add(uid)
            Block -> FriendAction.Block(uid)
            Decline -> FriendAction.Decline(uid, Out)
            Delete -> FriendAction.Delete(uid)
            Unblock -> FriendAction.Unblock(uid)
            else -> null
        }
    }
}
