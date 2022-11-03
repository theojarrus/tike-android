package com.theost.tike.feature.blacklist.presentation

import com.theost.tike.common.extension.filterItem
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.common.recycler.element.user.UserUi
import com.theost.tike.common.recycler.element.user.UserUiMapper
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.core.model.StateStatus.Error
import com.theost.tike.core.model.StateStatus.Success
import com.theost.tike.core.presentation.BaseStateViewModel
import com.theost.tike.core.presentation.SearchViewModel
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.UsersRepository
import com.theost.tike.feature.blacklist.business.ObserveBlacklistInteractor

class BlacklistViewModel : BaseStateViewModel<BlacklistState>(), SearchViewModel {

    private val mapper = UserUiMapper()

    private val observerBlacklistInteractor = ObserveBlacklistInteractor(
        AuthRepository,
        UsersRepository,
        mapper
    )

    fun observeBlacklist() {
        update { copy(status = status.getLoadingStatus()) }
        disposableSwitch {
            observerBlacklistInteractor()
                .subscribe({ users ->
                    update {
                        copy(
                            status = Success,
                            items = filterItems(users, state.value?.query),
                            cache = users
                        )
                    }
                }, { error ->
                    update { copy(status = Error, items = emptyList(), cache = emptyList()) }
                    log(this, error)
                })
        }
    }

    private fun filterItems(items: List<DelegateItem>?, query: String?): List<DelegateItem> {
        return items.filterItem<DelegateItem, UserUi> { it.isRespondQuery(query) }
    }

    override fun search(query: String) {
        update { copy(query = query) }
        if (status == Success) {
            update { copy(items = filterItems(state.value?.cache, query)) }
        }
    }

    override fun completeSearch() {
        update { copy(items = cache, query = null) }
    }
}
