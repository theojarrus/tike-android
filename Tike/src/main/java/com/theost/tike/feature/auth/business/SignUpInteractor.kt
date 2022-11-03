package com.theost.tike.feature.auth.business

import com.theost.tike.domain.model.core.User
import com.theost.tike.domain.model.dto.mapper.UserDtoMapper
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.Completable
import io.reactivex.Single

class SignUpInteractor(
    private val usersRepository: UsersRepository,
    private val mapper: UserDtoMapper
) {

    operator fun invoke(user: User): Completable {
        return Single.just(user)
            .map(mapper)
            .flatMapCompletable { usersRepository.addUser(it) }
    }
}
