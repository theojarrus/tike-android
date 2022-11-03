package com.theost.tike.feature.dialogs.presentation

import com.theost.tike.common.extension.mapList
import com.theost.tike.common.recycler.element.user.UserUiMapper
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.core.model.StateStatus.Error
import com.theost.tike.core.model.StateStatus.Success
import com.theost.tike.core.presentation.BaseStateViewModel
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.UsersRepository
import com.theost.tike.feature.dialogs.business.FetchDialogsInteractor

class DialogsViewModel : BaseStateViewModel<DialogsState>() {

    private val mapper = UserUiMapper()

    private val fetchDialogsInteractor = FetchDialogsInteractor(
        AuthRepository,
        UsersRepository,
        mapper
    )

    fun fetchDialogs() {
        update { copy(status = status.getLoadingStatus()) }
        disposableSwitch {
            fetchDialogsInteractor()
                .mapList { it.copy(nick = "Вы: чат создан") }
                .subscribe({ dialogs ->
                    update { copy(status = Success, items = dialogs) }
                }, { error ->
                    update { copy(status = Error, items = emptyList()) }
                    log(this, error)
                })
        }
    }
}
