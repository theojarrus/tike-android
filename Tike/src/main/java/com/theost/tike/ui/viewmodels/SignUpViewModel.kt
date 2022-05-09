package com.theost.tike.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.state.ExistStatus
import com.theost.tike.data.models.state.ExistStatus.Exist
import com.theost.tike.data.models.state.Status.*
import com.theost.tike.data.models.ui.LifestyleUi
import com.theost.tike.data.models.ui.mapToLifestyleUi
import com.theost.tike.data.repositories.LifestylesRepository
import com.theost.tike.data.repositories.UsersRepository
import com.theost.tike.ui.widgets.ExistException
import com.theost.tike.ui.utils.LogUtils.LOG_VIEW_MODEL_SIGN_UP
import com.theost.tike.ui.utils.StringUtils.formatName
import com.theost.tike.ui.utils.StringUtils.formatNameLetterCase
import com.theost.tike.ui.utils.StringUtils.formatNick
import com.theost.tike.ui.utils.StringUtils.formatNickLetterCase
import com.theost.tike.ui.utils.StringUtils.isNameCorrect
import com.theost.tike.ui.utils.StringUtils.isNickCorrect
import io.reactivex.disposables.CompositeDisposable

class SignUpViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _nickExistStatus = MutableLiveData<ExistStatus>()
    val nickExistStatus: LiveData<ExistStatus> = _nickExistStatus

    private val _nameStatus = MutableLiveData<Status>()
    val nameStatus: LiveData<Status> = _nameStatus

    private val _nickStatus = MutableLiveData<Status>()
    val nickStatus: LiveData<Status> = _nickStatus

    private val _lifestyles = MutableLiveData<List<LifestyleUi>>()
    val lifestyles: LiveData<List<LifestyleUi>> = _lifestyles

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _userNick = MutableLiveData<String>()
    val userNick: LiveData<String> = _userNick

    private var userId: String = ""
    private var userEmail: String = ""
    private var userPhone: String = ""
    private var userAvatar: String = ""

    private val selectedLifestyles: MutableList<String> = mutableListOf()

    private val compositeDisposable = CompositeDisposable()

    fun init() {
        restoreFirebaseUser()
        loadLifecycles()
    }

    private fun restoreFirebaseUser() {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).subscribe({ user ->
                if (_userName.value == null) _userName.value = formatName(user.displayName.orEmpty())
                if (_userNick.value == null) _userNick.value = formatNick(user.displayName.orEmpty())
                userId = user.uid
                userEmail = user.email.orEmpty()
                userPhone = user.phoneNumber.orEmpty()
                userAvatar = user.photoUrl.toString()
                _loadingStatus.postValue(Success)
            }, {
                _loadingStatus.postValue(Error)
            })
        )
    }

    private fun loadLifecycles() {
        compositeDisposable.add(
            LifestylesRepository.getLifestyles().subscribe({ styles ->
                _lifestyles.postValue(styles.map { lifestyle -> lifestyle.mapToLifestyleUi() })
            }, { error ->
                Log.e(LOG_VIEW_MODEL_SIGN_UP, error.toString())
            })
        )
    }

    fun selectLifestyle(id: String) {
        lifestyles.value?.let { lifestyles ->
            selectedLifestyles.apply { if (contains(id)) remove(id) else add(id) }
            _lifestyles.postValue(lifestyles.map { lifestyle ->
                lifestyle.copy(isSelected = selectedLifestyles.contains(lifestyle.id))
            })
        }
    }

    fun signUp(name: String, nick: String) {
        val isNameCorrect = isNameCorrect(name)
        val isNickCorrect = isNickCorrect(nick)
        val formattedName = formatNameLetterCase(name)
        val formattedNick = formatNickLetterCase(nick)
        _userName.value = formattedName
        _userNick.value = formattedNick
        if (isNameCorrect && isNickCorrect) {
            _loadingStatus.postValue(Loading)
            _nameStatus.postValue(Success)
            _nickStatus.postValue(Success)
            compositeDisposable.add(
                UsersRepository.addUser(
                    uid = userId,
                    name = formattedName,
                    nick = formattedNick,
                    email = userEmail,
                    phone = userPhone,
                    avatar = userAvatar,
                    lifestyles = selectedLifestyles
                ).subscribe({
                    _loadingStatus.postValue(Success)
                }, { error ->
                    if (error is ExistException) _nickExistStatus.postValue(Exist)
                    _loadingStatus.postValue(Error)
                    Log.e(LOG_VIEW_MODEL_SIGN_UP, error.toString())
                })
            )
        } else {
            if (!isNameCorrect) _nameStatus.postValue(Error)
            if (!isNickCorrect) _nickStatus.postValue(Error)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}