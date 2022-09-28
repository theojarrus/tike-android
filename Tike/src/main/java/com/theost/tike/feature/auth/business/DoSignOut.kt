package com.theost.tike.feature.auth.business

import com.theost.tike.domain.repository.AuthRepository
import io.reactivex.Completable

class DoSignOut(private val authRepository: AuthRepository) {

    operator fun invoke(): Completable {
        return authRepository.signOut()
    }
}
