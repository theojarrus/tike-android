package com.theost.tike.feature.dialogs.presentation

import com.theost.tike.common.extension.mapList
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.core.component.model.StateStatus.Error
import com.theost.tike.core.component.model.StateStatus.Success
import com.theost.tike.core.component.presentation.BaseStateViewModel
import com.theost.tike.core.recycler.user.UserToUserUiMapper
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.UsersRepository
import com.theost.tike.feature.dialogs.business.FetchDialogs

class DialogsViewModel : BaseStateViewModel<DialogsState>() {

    private val mapper = UserToUserUiMapper()

    fun fetchDialogs() {
        update { copy(status = status.getLoadingStatus()) }
        disposableSwitch {
            FetchDialogs(AuthRepository, UsersRepository, mapper).invoke()
                .mapList { copy(nick = "Вы: чат создан") }
                .subscribe({ dialogs ->
                    update { copy(status = Success, items = dialogs) }
                }, { error ->
                    log(this, error)
                    update { copy(status = Error, items = emptyList()) }
                })
        }
    }
}
