package com.theost.tike.data.models.state

import com.theost.tike.data.models.ui.UserUi

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
        val participants: List<UserUi>,
        val mode: EventMode
    ) : EventAction()
}
