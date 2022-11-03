package com.theost.tike.feature.inbox.business

import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.domain.model.multi.Direction.In
import com.theost.tike.domain.model.multi.Direction.Out
import com.theost.tike.domain.model.multi.EventType.Pending
import com.theost.tike.domain.model.multi.EventType.Requesting
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.UsersRepository
import com.theost.tike.feature.inbox.mapper.InboxMapper
import com.theost.tike.feature.inbox.model.InboxList
import io.reactivex.Observable

class ObserveInboxInteractor(
    private val authRepository: AuthRepository,
    private val usersRepository: UsersRepository,
    private val observeFriendsInteractor: ObserveInboxFriendsInteractor,
    private val observeEventsInteractor: ObserveInboxEventsInteractor,
    private val mapper: InboxMapper
) {

    operator fun invoke(): Observable<InboxList<DelegateItem>> {
        return authRepository.getActualUser()
            .flatMapObservable { firebaseUser ->
                usersRepository.observeUser(firebaseUser.uid)
                    .switchMap { databaseUser ->
                        Observable.combineLatest(
                            observeFriendsInteractor(databaseUser.uid, databaseUser.pending, In),
                            observeFriendsInteractor(databaseUser.uid, databaseUser.requesting, Out),
                            observeEventsInteractor(databaseUser.uid, Pending(In)),
                            observeEventsInteractor(databaseUser.uid, Requesting(In)),
                            observeEventsInteractor(databaseUser.uid, Pending(Out)),
                            observeEventsInteractor(databaseUser.uid, Requesting(Out))
                        ) { pendingFriends,
                            requestingFriends,
                            pendingInEvents,
                            requestingInEvents,
                            pendingOutEvents,
                            requestingOutEvents ->
                            mapper(
                                pendingFriends = pendingFriends,
                                requestingFriends = requestingFriends,
                                pendingInEvents = pendingInEvents,
                                requestingInEvents = requestingInEvents,
                                pendingOutEvents = pendingOutEvents,
                                requestingOutEvents = requestingOutEvents
                            )
                        }
                    }
            }
    }
}
