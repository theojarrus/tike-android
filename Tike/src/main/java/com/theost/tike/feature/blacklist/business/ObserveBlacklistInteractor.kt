package com.theost.tike.feature.blacklist.business

import com.theost.tike.common.extension.mapList
import com.theost.tike.common.recycler.element.user.UserUi
import com.theost.tike.common.recycler.element.user.UserUiMapper
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.Observable

class ObserveBlacklistInteractor(
    private val authRepository: AuthRepository,
    private val usersRepository: UsersRepository,
    private val mapper: UserUiMapper
) {

    operator fun invoke(): Observable<List<UserUi>> {
        return authRepository.getActualUser()
            .flatMapObservable { firebaseUser ->
                usersRepository.observeUser(firebaseUser.uid)
                    .switchMap { usersRepository.observeUsers(it.blocked) }
                    .mapList { mapper(it, firebaseUser.uid) }
            }
    }
}
