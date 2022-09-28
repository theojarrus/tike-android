package com.theost.tike.feature.auth.presentation

import com.theost.tike.common.exception.AuthException
import com.theost.tike.common.exception.ExistException
import com.theost.tike.common.extension.appendExcluding
import com.theost.tike.common.recycler.element.lifestyle.LifestyleToLifestyleUiMapper
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.common.util.StringUtils.formatNameLetterCase
import com.theost.tike.common.util.StringUtils.formatNickLetterCase
import com.theost.tike.common.util.StringUtils.isNameCorrect
import com.theost.tike.common.util.StringUtils.isNickCorrect
import com.theost.tike.core.model.StateStatus.*
import com.theost.tike.core.presentation.BaseStateViewModel
import com.theost.tike.domain.model.core.mapper.FirebaseUserToUserMapper
import com.theost.tike.domain.model.dto.mapper.UserToUserDtoMapper
import com.theost.tike.domain.model.multi.AuthStatus.SignedIn
import com.theost.tike.domain.model.multi.AuthStatus.SignedOut
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.LifestylesRepository
import com.theost.tike.domain.repository.UsersRepository
import com.theost.tike.feature.auth.business.*

class SignUpViewModel : BaseStateViewModel<SignUpState>() {

    private val userMapper = FirebaseUserToUserMapper()
    private val lifestyleMapper = LifestyleToLifestyleUiMapper()
    private val dtoMapper = UserToUserDtoMapper()

    fun fetchUser() {
        disposable {
            FetchUser(AuthRepository, userMapper).invoke()
                .subscribe({ user ->
                    update { copy(user = user) }
                }, { error ->
                    log(this, error)
                })
        }
    }

    fun fetchLifestyles() {
        disposable {
            FetchLifestyles(LifestylesRepository, lifestyleMapper).invoke()
                .subscribe({ lifestyles ->
                    update { copy(lifestyles = lifestyles) }
                }, { error ->
                    log(this, error)
                })
        }
    }

    fun updateLifestyle(id: String) {
        state.value?.lifestyles?.let { lifestyles ->
            val selected = state.value?.userLifestyles.orEmpty().appendExcluding(id)
            val mapped = lifestyles.map { it.copy(isSelected = selected.contains(it.id)) }
            update { copy(lifestyles = mapped, userLifestyles = selected) }
        }
    }

    fun signOut() {
        update { copy(status = Loading) }
        disposableSwitch {
            DoSignOut(AuthRepository).invoke()
                .subscribe({
                    update { copy(status = Success, authStatus = SignedOut) }
                }, { error ->
                    log(this, error)
                    update { copy(status = Success, authStatus = SignedOut) }
                })
        }
    }

    fun signUp(name: String, nick: String) {
        val isNameCorrect = isNameCorrect(name)
        val isNickCorrect = isNickCorrect(nick)
        val formatName = formatNameLetterCase(name)
        val formatNick = formatNickLetterCase(nick)
        if (isNameCorrect && isNickCorrect) {
            state.value?.user?.let { user ->
                update {
                    copy(
                        status = Loading,
                        user = user.copy(name = formatName.trim(), nick = formatNick.trim())
                    )
                }
                disposableSwitch {
                    FetchAuthStatus(AuthRepository).invoke()
                        .flatMapCompletable { DoSignUp(UsersRepository, dtoMapper).invoke(user) }
                        .subscribe({
                            update { copy(status = Success, authStatus = SignedIn) }
                        }, { error ->
                            log(this, error)
                            update { copy(status = Error, isUserExist = error is ExistException) }
                        })
                }
            } ?: run {
                log(this, AuthException())
                update { copy(status = Error) }
            }
        } else {
            update {
                copy(
                    status = Error,
                    user = user.copy(name = formatName, nick = formatNick),
                    isNameError = !isNameCorrect,
                    isNickError = !isNickCorrect
                )
            }
        }
    }
}
