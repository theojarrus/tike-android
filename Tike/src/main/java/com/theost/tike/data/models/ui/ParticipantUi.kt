package com.theost.tike.data.models.ui

import com.theost.tike.data.models.core.User
import com.theost.tike.ui.interfaces.DelegateItem

data class ParticipantUi(
    val id: String,
    val name: String,
    val avatar: String
) : DelegateItem {
    override fun id(): Any = id
    override fun content(): Any = name
}

fun User.mapToParticipantUi(): ParticipantUi {
    return ParticipantUi(
        id = id,
        name = name,
        avatar = avatar
    )
}