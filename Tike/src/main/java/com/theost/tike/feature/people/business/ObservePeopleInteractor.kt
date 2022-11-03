package com.theost.tike.feature.people.business

import com.theost.tike.common.extension.filterList
import com.theost.tike.common.extension.mapList
import com.theost.tike.common.extension.mergeWith
import com.theost.tike.common.recycler.element.user.UserUi
import com.theost.tike.common.recycler.element.user.UserUiMapper
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.Observable

class ObservePeopleInteractor(
    private val authRepository: AuthRepository,
    private val usersRepository: UsersRepository,
    private val mapper: UserUiMapper
) {

    operator fun invoke(): Observable<List<UserUi>> {
        return authRepository.getActualUser()
            .flatMapObservable { firebaseUser ->
                usersRepository.observeUser(firebaseUser.uid)
                    .switchMap { usersRepository.observeAllUsers(it.friends.mergeWith(it.blocked)) }
                    .filterList { it.uid != firebaseUser.uid }
                    .mapList { mapper(it, firebaseUser.uid) }
            }
    }
}
