package com.theost.tike.feature.auth.business

import com.google.firebase.auth.AuthCredential
import com.theost.tike.domain.model.multi.AuthStatus
import com.theost.tike.domain.repository.AuthRepository
import io.reactivex.Single

class FetchSignInInteractor(private val authRepository: AuthRepository) {

    operator fun invoke(credential: AuthCredential): Single<AuthStatus> {
        return authRepository.signIn(credential)
            .flatMap { authRepository.getUserAuthStatus() }
    }
}
