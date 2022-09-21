package com.theost.tike.domain.model.multi

import com.theost.tike.core.recycler.user.UserUi

sealed class EventAction {

    class Accept(
        val id: String,
        val creator: String,
        val participants: List<UserUi>,
        val mode: EventMode
    ) : EventAction()

    class Reject(
        val id: String,
        val creator: String,
        val participants: List<UserUi>,
        val mode: EventMode
    ) : EventAction()

    class Info(
        val id: String,
        val creator: String,
        val participants: List<UserUi>
    ) : EventAction()
}
