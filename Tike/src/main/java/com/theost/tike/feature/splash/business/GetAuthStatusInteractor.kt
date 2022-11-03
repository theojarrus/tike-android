package com.theost.tike.feature.splash.business

import com.theost.tike.domain.model.multi.AuthStatus
import com.theost.tike.domain.repository.AuthRepository
import io.reactivex.Single

class GetAuthStatusInteractor(private val authRepository: AuthRepository) {

    operator fun invoke(): Single<AuthStatus> {
        return authRepository.getUserAuthStatus()
    }
}
