package com.theost.tike.feature.inbox.business

import com.theost.tike.common.extension.mapList
import com.theost.tike.common.recycler.element.friend.FriendUi
import com.theost.tike.common.recycler.element.friend.FriendUiMapper
import com.theost.tike.domain.model.multi.Direction
import com.theost.tike.domain.repository.UsersRepository
import com.theost.tike.feature.inbox.model.InboxList
import io.reactivex.Observable

class ObserveInboxFriendsInteractor(
    private val usersRepository: UsersRepository,
    private val mapper: FriendUiMapper
) {

    operator fun invoke(
        auid: String,
        uids: List<String>,
        direction: Direction
    ): Observable<InboxList<FriendUi>> {
        return Observable.concat(
            Observable.just(InboxList()),
            usersRepository.observeUsers(uids)
                .mapList { mapper(it, auid, direction) }
                .map(::InboxList)
        )
    }
}
