package com.theost.tike.feature.dialogs.business

import com.theost.tike.common.extension.mapList
import com.theost.tike.core.recycler.user.UserToUserUiMapper
import com.theost.tike.core.recycler.user.UserUi
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.Single

class FetchDialogs(
    private val authRepository: AuthRepository,
    private val usersRepository: UsersRepository,
    private val mapper: UserToUserUiMapper
) {

    operator fun invoke(): Single<List<UserUi>> {
        return authRepository.getCurrentUser()
            .flatMapSingle { firebaseUser ->
                usersRepository.getUser(firebaseUser.uid)
                    .flatMap { usersRepository.getUsers(it.friends) }
                    .mapList { mapper.invoke(this, firebaseUser.uid) }
            }
    }
}
