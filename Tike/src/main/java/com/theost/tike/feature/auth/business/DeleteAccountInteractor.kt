package com.theost.tike.feature.auth.business

import com.google.firebase.auth.AuthCredential
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.CacheRepository
import io.reactivex.Completable

class DeleteAccountInteractor(
    private val authRepository: AuthRepository,
    private val cacheRepository: CacheRepository
) {

    operator fun invoke(credential: AuthCredential): Completable {
        return cacheRepository.clearCache().onErrorComplete()
            .andThen(authRepository.delete(credential))
    }
}
