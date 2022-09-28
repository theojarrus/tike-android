package com.theost.tike.feature.people.presentation

import com.theost.tike.common.extension.filterWith
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.core.component.model.StateStatus.Error
import com.theost.tike.core.component.model.StateStatus.Success
import com.theost.tike.core.component.presentation.BaseStateViewModel
import com.theost.tike.core.component.presentation.SearchViewModel
import com.theost.tike.core.recycler.user.UserToUserUiMapper
import com.theost.tike.core.recycler.user.UserUi
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.UsersRepository
import com.theost.tike.feature.people.business.ObservePeople

class PeopleViewModel : BaseStateViewModel<PeopleState>(), SearchViewModel {

    private val mapper = UserToUserUiMapper()

    fun observePeople() {
        update { copy(status = status.getLoadingStatus()) }
        disposableSwitch {
            ObservePeople(AuthRepository, UsersRepository, mapper).invoke()
                .subscribe({ users ->
                    update {
                        copy(
                            status = Success,
                            items = filterItems(users, state.value?.query),
                            cache = users
                        )
                    }
                }, { error ->
                    log(this, error)
                    update { copy(status = Error, items = emptyList(), cache = emptyList()) }
                })
        }
    }

    private fun filterItems(items: List<DelegateItem>?, query: String?): List<DelegateItem> {
        return items.orEmpty().filterWith(query) { value, element ->
            element is UserUi && element.isRespondQuery(value)
        }
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
