package com.theost.tike.feature.inbox.mapper

import com.theost.tike.R
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.common.recycler.element.event.EventUi
import com.theost.tike.common.recycler.element.friend.FriendUi
import com.theost.tike.common.recycler.element.title.TitleUi
import com.theost.tike.feature.inbox.model.InboxList

class InboxMapper : (
    InboxList<FriendUi>,
    InboxList<FriendUi>,
    InboxList<EventUi>,
    InboxList<EventUi>,
    InboxList<EventUi>,
    InboxList<EventUi>
) -> InboxList<DelegateItem> {

    override fun invoke(
        pendingFriends: InboxList<FriendUi>,
        requestingFriends: InboxList<FriendUi>,
        pendingInEvents: InboxList<EventUi>,
        requestingInEvents: InboxList<EventUi>,
        pendingOutEvents: InboxList<EventUi>,
        requestingOutEvents: InboxList<EventUi>
    ): InboxList<DelegateItem> {
        val inboxLists = listOf(
            pendingFriends,
            requestingFriends,
            pendingInEvents,
            requestingInEvents,
            pendingOutEvents,
            requestingOutEvents
        ).map(InboxList<out DelegateItem?>::value)
        val isNotNull = { !inboxLists.contains(null) }
        val isNotEmpty = { inboxLists.filterNot(List<DelegateItem?>?::isNullOrEmpty).isNotEmpty() }
        return if (isNotNull() || isNotEmpty()) {
            val items = buildList {
                if (!pendingFriends.value.isNullOrEmpty()) {
                    add(TitleUi(R.string.pending_friends))
                    addAll(pendingFriends.value)
                }
                if (!pendingInEvents.value.isNullOrEmpty()) {
                    add(TitleUi(R.string.pending_in_events))
                    addAll(pendingInEvents.value)
                }
                if (!requestingInEvents.value.isNullOrEmpty()) {
                    add(TitleUi(R.string.requesting_in_events))
                    addAll(requestingInEvents.value)
                }
                if (!requestingFriends.value.isNullOrEmpty()) {
                    add(TitleUi(R.string.requesting_friends))
                    addAll(requestingFriends.value)
                }
                if (!pendingOutEvents.value.isNullOrEmpty()) {
                    add(TitleUi(R.string.pending_out_events))
                    addAll(pendingOutEvents.value)
                }
                if (!requestingOutEvents.value.isNullOrEmpty()) {
                    add(TitleUi(R.string.requesting_out_events))
                    addAll(requestingOutEvents.value)
                }
            }
            InboxList(items)
        } else {
            InboxList()
        }
    }
}
