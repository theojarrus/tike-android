package com.theost.tike.feature.profile.presentation

import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.core.model.StateStatus.*
import com.theost.tike.core.presentation.BaseStateViewModel
import com.theost.tike.domain.model.multi.FriendAction
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.FriendsRepository
import com.theost.tike.domain.repository.UsersRepository
import com.theost.tike.feature.actions.FriendActionValidator
import com.theost.tike.feature.actions.UpdateFriendInteractor
import com.theost.tike.feature.profile.business.ObserveProfileInteractor
import com.theost.tike.feature.profile.ui.mapper.ProfileUiMapper

class ProfileViewModel : BaseStateViewModel<ProfileState>() {

    private val mapper = ProfileUiMapper()

    private val friendActionValidator = FriendActionValidator()

    private val observeProfileInteractor = ObserveProfileInteractor(
        AuthRepository,
        UsersRepository,
        mapper
    )

    private val updateFriendInteractor = UpdateFriendInteractor(
        AuthRepository,
        UsersRepository,
        FriendsRepository,
        friendActionValidator
    )

    fun observeProfile(uid: String?) {
        update { copy(status = status.getLoadingStatus()) }
        disposableSwitch {
            observeProfileInteractor(uid)
                .subscribe({ profile ->
                    update { copy(status = Success, profile = profile) }
                }, { error ->
                    update { copy(status = Error, profile = null) }
                    log(this, error)
                })
        }
    }

    fun dispatchFriendAction(friendAction: FriendAction) {
        update { copy(status = Loading) }
        dispose()
        disposable {
            updateFriendInteractor(friendAction)
                .subscribe({
                    observeProfile(state.value?.profile?.uid)
                }, { error ->
                    update { copy(status = Error, profile = null) }
                    log(this, error)
                })
        }
    }
}
