package com.theost.tike.feature.profile.business

import com.theost.tike.domain.model.core.User
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.UsersRepository
import com.theost.tike.feature.profile.ui.mapper.ProfileUiMapper
import com.theost.tike.feature.profile.ui.model.ProfileUi
import io.reactivex.Observable

class ObserveProfileInteractor(
    private val authRepository: AuthRepository,
    private val usersRepository: UsersRepository,
    private val mapper: ProfileUiMapper
) {

    operator fun invoke(uid: String?): Observable<ProfileUi> {
        return authRepository.getActualUser()
            .flatMapObservable { firebaseUser ->
                usersRepository.observeUser(firebaseUser.uid)
                    .switchMap { actualUser ->
                        getUserObservable(uid, actualUser)
                            .distinctUntilChanged()
                            .map { mapper(it, actualUser) }
                    }
            }
    }

    private fun getUserObservable(uid: String?, actualUser: User): Observable<User> {
        return if (uid == null || uid == actualUser.uid) {
            Observable.just(actualUser)
        } else {
            usersRepository.observeUser(uid)
        }
    }
}
