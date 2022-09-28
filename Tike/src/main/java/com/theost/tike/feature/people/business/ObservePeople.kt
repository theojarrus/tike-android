package com.theost.tike.feature.people.business

import com.theost.tike.common.extension.mapList
import com.theost.tike.common.recycler.element.user.UserToUserUiMapper
import com.theost.tike.common.recycler.element.user.UserUi
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.Observable

class ObservePeople(
    private val authRepository: AuthRepository,
    private val usersRepository: UsersRepository,
    private val mapper: UserToUserUiMapper
) {

    operator fun invoke(): Observable<List<UserUi>> {
        return authRepository.getCurrentUser()
            .flatMapObservable { firebaseUser ->
                usersRepository.observeUser(firebaseUser.uid)
                    .switchMap { usersRepository.observeAllUsers(it.blocked) }
                    .mapList { mapper.invoke(this, firebaseUser.uid) }
            }
    }
}
