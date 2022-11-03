package com.theost.tike.feature.auth.business

import com.theost.tike.domain.model.multi.AuthStatus
import com.theost.tike.domain.repository.AuthRepository
import io.reactivex.Single

class FetchAuthStatusInteractor(private val authRepository: AuthRepository) {

    operator fun invoke(): Single<AuthStatus> {
        return authRepository.getRemoteUserAuthStatus()
    }
}
