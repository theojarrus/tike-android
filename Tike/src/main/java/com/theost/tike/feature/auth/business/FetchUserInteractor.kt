package com.theost.tike.feature.auth.business

import com.theost.tike.domain.model.core.User
import com.theost.tike.domain.model.core.mapper.UserFirebaseMapper
import com.theost.tike.domain.repository.AuthRepository
import io.reactivex.Single

class FetchUserInteractor(
    private val authRepository: AuthRepository,
    private val mapper: UserFirebaseMapper
) {

    operator fun invoke(): Single<User> {
        return authRepository.getActualUser()
            .map(mapper)
    }
}
