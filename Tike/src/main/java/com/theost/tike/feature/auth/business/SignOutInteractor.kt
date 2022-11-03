package com.theost.tike.feature.auth.business

import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.CacheRepository
import io.reactivex.Completable

class SignOutInteractor(
    private val authRepository: AuthRepository,
    private val cacheRepository: CacheRepository
) {

    operator fun invoke(): Completable {
        return cacheRepository.clearCache().onErrorComplete()
            .andThen(authRepository.signOut())
    }
}
