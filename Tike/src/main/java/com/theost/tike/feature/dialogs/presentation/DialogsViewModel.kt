package com.theost.tike.feature.dialogs.presentation

import com.theost.tike.common.extension.mapList
import com.theost.tike.common.util.LogUtils.logError
import com.theost.tike.core.component.model.StateStatus.*
import com.theost.tike.core.component.presentation.BaseRxStateViewModel
import com.theost.tike.core.recycler.user.UserToUserUiMapper
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.UsersRepository
import com.theost.tike.feature.dialogs.business.GetCurrentUserFriends
import java.util.concurrent.TimeUnit.MILLISECONDS

class DialogsViewModel : BaseRxStateViewModel<DialogsState>() {

    private val mapper = UserToUserUiMapper()

    fun fetchDialogs() {
        update { copy(status = status.getLoadingStatus()) }
        disposable {
            GetCurrentUserFriends(AuthRepository, UsersRepository, mapper).invoke()
                .mapList { copy(nick = "Вы: чат создан") }
                .delay(3000, MILLISECONDS)
                .subscribe({ dialogs ->
                    update { copy(status = Success, items = dialogs) }
                }, { error ->
                    update { copy(status = Error, items = emptyList()) }
                    logError(this, error)
                })
        }
    }
}
