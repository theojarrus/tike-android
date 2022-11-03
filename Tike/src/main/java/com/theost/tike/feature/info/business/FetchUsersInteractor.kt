package com.theost.tike.feature.info.business

import com.theost.tike.common.extension.mapList
import com.theost.tike.common.recycler.element.user.UserUi
import com.theost.tike.common.recycler.element.user.UserUiMapper
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.Single

class FetchUsersInteractor(
    private val usersRepository: UsersRepository,
    private val mapper: UserUiMapper
) {

    operator fun invoke(ids: List<String>, auid: String): Single<List<UserUi>> {
        return usersRepository.getUsers(ids)
            .mapList { mapper(it, auid) }
    }
}
