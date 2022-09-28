package com.theost.tike.feature.auth.business

import com.theost.tike.domain.model.core.User
import com.theost.tike.domain.model.dto.mapper.UserToUserDtoMapper
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.Completable

class DoSignUp(
    private val usersRepository: UsersRepository,
    private val mapper: UserToUserDtoMapper
) {

    operator fun invoke(user: User): Completable {
        return mapper(user)
            .run { usersRepository.addUser(this) }
    }
}
