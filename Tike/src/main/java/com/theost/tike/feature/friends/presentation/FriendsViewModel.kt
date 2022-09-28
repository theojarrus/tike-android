package com.theost.tike.feature.friends.presentation

import com.theost.tike.common.extension.filterWith
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.common.recycler.element.user.UserToUserUiMapper
import com.theost.tike.common.recycler.element.user.UserUi
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.core.model.StateStatus.Error
import com.theost.tike.core.model.StateStatus.Success
import com.theost.tike.core.presentation.BaseStateViewModel
import com.theost.tike.core.presentation.SearchViewModel
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.UsersRepository
import com.theost.tike.feature.friends.business.ObserveFriends

class FriendsViewModel : BaseStateViewModel<FriendsState>(), SearchViewModel {

    private val mapper = UserToUserUiMapper()

    fun observeFriends() {
        update { copy(status = status.getLoadingStatus()) }
        disposableSwitch {
            ObserveFriends(AuthRepository, UsersRepository, mapper).invoke()
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
