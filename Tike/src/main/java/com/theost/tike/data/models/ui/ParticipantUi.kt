package com.theost.tike.data.models.ui

import com.theost.tike.data.models.core.User
import com.theost.tike.ui.interfaces.DelegateItem

data class ParticipantUi(
    val uid: String,
    val name: String,
    val avatar: String
) : DelegateItem {
    override fun id(): Any = uid
    override fun content(): Any = name
}

fun User.mapToParticipantUi(): ParticipantUi {
    return ParticipantUi(
        uid = uid,
        name = name,
        avatar = avatar
    )
}