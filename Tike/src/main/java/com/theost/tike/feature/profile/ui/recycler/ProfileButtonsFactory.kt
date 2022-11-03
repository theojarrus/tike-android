package com.theost.tike.feature.profile.ui.recycler

import com.theost.tike.R
import com.theost.tike.common.recycler.element.button.ButtonUi
import com.theost.tike.domain.model.multi.ButtonStyle.Filled
import com.theost.tike.domain.model.multi.ButtonStyle.Outlined
import com.theost.tike.domain.model.multi.FriendStatus
import com.theost.tike.domain.model.multi.FriendStatus.*
import com.theost.tike.feature.profile.ui.model.ProfileAction
import com.theost.tike.feature.profile.ui.model.ProfileAction.*

class ProfileButtonsFactory {

    fun getControlButtons(isActive: Boolean?, isActual: Boolean?): List<ButtonUi<ProfileAction>> {
        return buildList {
            if (isActive != true || isActual == null) return@buildList
            add(ButtonUi(action = Share, icon = R.drawable.ic_share, style = Outlined))
            add(ButtonUi(action = Qr, icon = R.drawable.ic_qr_code, style = Outlined))
            if (isActual) {
                add(ButtonUi(action = Edit, icon = R.drawable.ic_edit, style = Outlined))
                add(ButtonUi(action = Friends, icon = R.drawable.ic_friends, style = Outlined))
                add(ButtonUi(action = Preferences, icon = R.drawable.ic_settings, style = Outlined))
            }
        }
    }

    fun getActionButtons(
        isActive: Boolean?,
        isBlocked: Boolean?,
        hasAccess: Boolean?,
        isActual: Boolean?,
        friendStatus: FriendStatus?
    ): List<ButtonUi<ProfileAction>> {
        return buildList {
            if (isActive != true || isActual == null || isBlocked == null || hasAccess == null || friendStatus == null) return@buildList
            if (!isActual && !isBlocked && hasAccess) {
                add(ButtonUi(action = Message, icon = R.drawable.ic_message, style = Filled))
                add(getFriendButton(friendStatus))
            }
        }
    }

    private fun getFriendButton(friendStatus: FriendStatus): ButtonUi<ProfileAction> {
        return when (friendStatus) {
            REQUESTING -> ButtonUi(
                action = Decline,
                text = R.string.request_sent,
                icon = R.drawable.ic_pending,
                style = Filled
            )
            PENDING -> ButtonUi(
                action = Accept,
                text = R.string.accept_request,
                icon = R.drawable.ic_requested,
                style = Filled
            )
            FRIEND -> ButtonUi(
                action = Delete,
                text = R.string.friend,
                icon = R.drawable.ic_friend,
                style = Filled
            )
            NOT_FRIEND -> ButtonUi(
                action = Add,
                text = R.string.add_friend,
                icon = R.drawable.ic_friend_add,
                style = Filled
            )
        }
    }
}
