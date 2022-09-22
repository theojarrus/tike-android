package com.theost.tike.feature.dialogs.business

import com.theost.tike.core.recycler.user.UserToUserUiMapper
import com.theost.tike.core.recycler.user.UserUi
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.Single

class GetCurrentUserFriends(
    private val authRepository: AuthRepository,
    private val usersRepository: UsersRepository,
    private val mapper: UserToUserUiMapper
) {

    operator fun invoke(): Single<List<UserUi>> {
        return authRepository.getCurrentUser()
            .flatMapSingle { firebaseUser ->
                usersRepository.getUser(firebaseUser.uid)
                    .flatMap { usersRepository.getUsers(it.friends) }
                    .map { items -> items.map { mapper.invoke(it, firebaseUser.uid) } }
            }
    }
}
