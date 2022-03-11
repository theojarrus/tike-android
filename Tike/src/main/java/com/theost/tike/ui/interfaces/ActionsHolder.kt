package com.theost.tike.ui.interfaces

typealias PeopleResultListener = (usersIds: List<String>) -> Unit

interface ActionsHolder {
    fun openParticipantsAdding(
        requestKey: String,
        bundleKey: String,
        addedIds: Set<String>,
        peopleResultListener: PeopleResultListener
    )
}