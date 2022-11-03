package com.theost.tike.feature.inbox.presentation

import com.theost.tike.common.extension.filterNotItem
import com.theost.tike.common.extension.subscribe
import com.theost.tike.common.recycler.element.event.EventUiMapper
import com.theost.tike.common.recycler.element.friend.FriendUi
import com.theost.tike.common.recycler.element.friend.FriendUiMapper
import com.theost.tike.common.recycler.element.user.UserUiMapper
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.core.model.StateStatus.Error
import com.theost.tike.core.model.StateStatus.Success
import com.theost.tike.core.presentation.BaseStateViewModel
import com.theost.tike.domain.model.multi.EventAction
import com.theost.tike.domain.model.multi.FriendAction
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.EventsRepository
import com.theost.tike.domain.repository.FriendsRepository
import com.theost.tike.domain.repository.UsersRepository
import com.theost.tike.feature.actions.EventActionValidator
import com.theost.tike.feature.actions.FriendActionValidator
import com.theost.tike.feature.actions.UpdateEventInteractor
import com.theost.tike.feature.actions.UpdateFriendInteractor
import com.theost.tike.feature.inbox.business.ObserveInboxEventsInteractor
import com.theost.tike.feature.inbox.business.ObserveInboxFriendsInteractor
import com.theost.tike.feature.inbox.business.ObserveInboxInteractor
import com.theost.tike.feature.inbox.mapper.InboxMapper

class InboxViewModel : BaseStateViewModel<InboxState>() {

    private val friendMapper = FriendUiMapper()
    private val userMapper = UserUiMapper()
    private val eventMapper = EventUiMapper()
    private val inboxMapper = InboxMapper()

    private val eventActionValidator = EventActionValidator()
    private val friendActionValidator = FriendActionValidator()

    private val observeInboxFriendsInteractor = ObserveInboxFriendsInteractor(
        UsersRepository,
        friendMapper
    )

    private val observeInboxEventsInteractor = ObserveInboxEventsInteractor(
        EventsRepository,
        UsersRepository,
        eventMapper,
        userMapper
    )

    private val observeInboxInteractor = ObserveInboxInteractor(
        AuthRepository,
        UsersRepository,
        observeInboxFriendsInteractor,
        observeInboxEventsInteractor,
        inboxMapper
    )

    private val updateFriendInteractor = UpdateFriendInteractor(
        AuthRepository,
        UsersRepository,
        FriendsRepository,
        friendActionValidator
    )

    private val updateEventInteractor = UpdateEventInteractor(
        AuthRepository,
        EventsRepository,
        eventActionValidator
    )

    fun observeInbox() {
        update { copy(status = status.getLoadingStatus()) }
        disposableSwitch {
            observeInboxInteractor()
                .subscribe({ items ->
                    if (items.value != null) update { copy(status = Success, items = items.value) }
                }, { error ->
                    update { copy(status = Error, items = emptyList()) }
                    log(this, error)
                })
        }
    }

    fun dispatchEventAction(eventAction: EventAction) {
        update { copy(items = items.filterNot { it.id() == eventAction.item }) }
        disposable {
            updateEventInteractor(eventAction)
                .subscribe { error ->
                    update { copy(status = Error, items = emptyList()) }
                    log(this, error)
                }
        }
    }

    fun dispatchFriendAction(friendAction: FriendAction) {
        update { copy(items = items.filterNotItem(FriendUi::uid, friendAction.uid)) }
        disposable {
            updateFriendInteractor(friendAction)
                .subscribe { error ->
                    update { copy(status = Error, items = emptyList()) }
                    log(this, error)
                }
        }
    }
}
