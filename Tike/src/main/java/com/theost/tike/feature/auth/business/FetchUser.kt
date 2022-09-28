package com.theost.tike.feature.auth.business

import com.theost.tike.domain.model.core.User
import com.theost.tike.domain.model.core.mapper.FirebaseUserToUserMapper
import com.theost.tike.domain.repository.AuthRepository
import io.reactivex.Single

class FetchUser(
    private val authRepository: AuthRepository,
    private val mapper: FirebaseUserToUserMapper
) {

    operator fun invoke(): Single<User> {
        return authRepository.getCurrentUser().toSingle()
            .map { mapper(it) }
    }
}
