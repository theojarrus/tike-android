package com.theost.tike.feature.info.presentation

import com.theost.tike.common.recycler.element.user.UserUiMapper
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.core.model.StateStatus.*
import com.theost.tike.core.presentation.BaseStateViewModel
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.EventsRepository
import com.theost.tike.domain.repository.UsersRepository
import com.theost.tike.feature.info.business.FetchEventInfoInteractor
import com.theost.tike.feature.info.business.FetchUserInteractor
import com.theost.tike.feature.info.business.FetchUsersInteractor
import com.theost.tike.feature.info.mapper.InfoMapper

class InfoViewModel : BaseStateViewModel<InfoState>() {

    private val userMapper = UserUiMapper()
    private val infoMapper = InfoMapper()

    private val fetchUserInteractor = FetchUserInteractor(UsersRepository, userMapper)
    private val fetchUsersInteractor = FetchUsersInteractor(UsersRepository, userMapper)
    private val fetchEventInfoInteractor = FetchEventInfoInteractor(
        AuthRepository,
        EventsRepository,
        fetchUserInteractor,
        fetchUsersInteractor,
        infoMapper
    )

    fun fetchEventInfo(id: String, creator: String) {
        update { copy(status = Loading) }
        disposableSwitch {
            fetchEventInfoInteractor(id, creator)
                .subscribe({ items ->
                    update { copy(status = Success, items = items) }
                }, { error ->
                    update { copy(status = Error, items = emptyList()) }
                    log(this, error)
                })
        }
    }
}
