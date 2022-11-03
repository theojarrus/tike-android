package com.theost.tike.feature.dialogs.business

import com.theost.tike.common.extension.mapList
import com.theost.tike.common.recycler.element.user.UserUi
import com.theost.tike.common.recycler.element.user.UserUiMapper
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.Single

class FetchDialogsInteractor(
    private val authRepository: AuthRepository,
    private val usersRepository: UsersRepository,
    private val mapper: UserUiMapper
) {

    operator fun invoke(): Single<List<UserUi>> {
        return authRepository.getActualUser()
            .flatMap { firebaseUser ->
                usersRepository.getUser(firebaseUser.uid)
                    .flatMap { usersRepository.getUsers(it.friends) }
                    .mapList { mapper(it, firebaseUser.uid) }
            }
    }
}
