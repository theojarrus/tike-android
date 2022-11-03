package com.theost.tike.feature.auth.presentation

import com.theost.tike.common.exception.AuthException
import com.theost.tike.common.exception.ExistException
import com.theost.tike.common.extension.appendExcluding
import com.theost.tike.common.recycler.element.lifestyle.LifestyleUiMapper
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.common.util.StringUtils.formatNameLetterCase
import com.theost.tike.common.util.StringUtils.formatNickLetterCase
import com.theost.tike.common.util.StringUtils.isNameCorrect
import com.theost.tike.common.util.StringUtils.isNickCorrect
import com.theost.tike.core.model.StateStatus.*
import com.theost.tike.core.presentation.BaseStateViewModel
import com.theost.tike.domain.model.core.mapper.UserFirebaseMapper
import com.theost.tike.domain.model.dto.mapper.UserDtoMapper
import com.theost.tike.domain.model.multi.AuthStatus.SignedIn
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.LifestylesRepository
import com.theost.tike.domain.repository.UsersRepository
import com.theost.tike.feature.auth.business.FetchAuthStatusInteractor
import com.theost.tike.feature.auth.business.FetchLifestylesInteractor
import com.theost.tike.feature.auth.business.FetchUserInteractor
import com.theost.tike.feature.auth.business.SignUpInteractor

class SignUpViewModel : BaseStateViewModel<SignUpState>() {

    private val userMapper = UserFirebaseMapper()
    private val lifestyleMapper = LifestyleUiMapper()
    private val dtoMapper = UserDtoMapper()

    private val signUpInteractor = SignUpInteractor(UsersRepository, dtoMapper)
    private val fetchAuthStatusInteractor = FetchAuthStatusInteractor(AuthRepository)
    private val fetchUserInteractor = FetchUserInteractor(AuthRepository, userMapper)
    private val fetchLifestylesInteractor = FetchLifestylesInteractor(
        LifestylesRepository,
        lifestyleMapper
    )

    fun fetchUser() {
        disposable {
            fetchUserInteractor()
                .subscribe({ user ->
                    update { copy(user = user) }
                }, { error ->
                    log(this, error)
                })
        }
    }

    fun fetchLifestyles() {
        disposable {
            fetchLifestylesInteractor()
                .subscribe({ lifestyles ->
                    update { copy(lifestyles = lifestyles) }
                }, { error ->
                    log(this, error)
                })
        }
    }

    fun updateLifestyle(id: String) {
        state.value?.lifestyles?.let { lifestyles ->
            val selected = state.value?.userLifestyles.appendExcluding(id)
            val mapped = lifestyles.map { it.copy(isSelected = selected.contains(it.id)) }
            update { copy(lifestyles = mapped, userLifestyles = selected) }
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
                    fetchAuthStatusInteractor()
                        .flatMapCompletable { signUpInteractor(user) }
                        .subscribe({
                            update { copy(status = Success, authStatus = SignedIn) }
                        }, { error ->
                            update { copy(status = Error, isUserExist = error is ExistException) }
                            log(this, error)
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
                    user = user?.copy(name = formatName, nick = formatNick),
                    isNameError = !isNameCorrect,
                    isNickError = !isNickCorrect
                )
            }
        }
    }
}
