package com.theost.tike.feature.info.business

import com.theost.tike.common.recycler.element.user.UserUi
import com.theost.tike.common.recycler.element.user.UserUiMapper
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.Single

class FetchUserInteractor(
    private val usersRepository: UsersRepository,
    private val mapper: UserUiMapper
) {

    operator fun invoke(uid: String, auid: String): Single<UserUi> {
        return usersRepository.getUser(uid)
            .map { mapper(it, auid) }
    }
}
