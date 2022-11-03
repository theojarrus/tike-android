package com.theost.tike.feature.info.business

import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.EventsRepository
import com.theost.tike.feature.info.mapper.InfoMapper
import io.reactivex.Single

class FetchEventInfoInteractor(
    private val authRepository: AuthRepository,
    private val eventsRepository: EventsRepository,
    private val fetchUserInteractor: FetchUserInteractor,
    private val fetchUsersInteractor: FetchUsersInteractor,
    private val mapper: InfoMapper
) {

    operator fun invoke(id: String, creator: String): Single<List<DelegateItem>> {
        return eventsRepository.getEvent(creator, id)
            .flatMap { event ->
                authRepository.getActualUser()
                    .flatMap { firebaseUser ->
                        Single.zip(
                            fetchUserInteractor(event.creatorId, firebaseUser.uid),
                            fetchUsersInteractor(event.participants, firebaseUser.uid),
                            fetchUsersInteractor(event.pending, firebaseUser.uid),
                            fetchUsersInteractor(event.requesting, firebaseUser.uid)
                        ) { creator, members, pending, requesting ->
                            mapper(
                                firebaseUser.uid,
                                creator,
                                event.location,
                                members,
                                pending,
                                requesting
                            )
                        }
                    }
            }
    }
}
