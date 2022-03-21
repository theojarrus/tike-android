package com.theost.tike.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.state.UserStatus
import com.theost.tike.data.models.ui.ListLifestyle
import com.theost.tike.data.models.ui.mapToListLifestyle
import com.theost.tike.data.repositories.LifestylesRepository
import com.theost.tike.data.repositories.UsersRepository
import com.theost.tike.utils.StringUtils
import io.reactivex.disposables.CompositeDisposable

class RegistrationViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _checkingStatus = MutableLiveData<Status>()
    val checkingStatus: LiveData<Status> = _checkingStatus

    private val _lifestyles = MutableLiveData<List<ListLifestyle>>()
    val lifestyles: LiveData<List<ListLifestyle>> = _lifestyles

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _userNick = MutableLiveData<String>()
    val userNick: LiveData<String> = _userNick

    private val userLifestyles: MutableList<String> = mutableListOf()
    private var userId: String = ""
    private var userEmail: String = ""
    private var userPhone: String = ""
    private var userAvatar: String = ""

    private val compositeDisposable = CompositeDisposable()

    init {
        _loadingStatus.postValue(Status.Loading)
        Firebase.auth.currentUser?.let { user ->
            _userName.postValue(user.displayName.orEmpty())
            _userNick.postValue(StringUtils.formatNick(user.displayName.orEmpty()))
            userId = user.uid
            userEmail = user.email.orEmpty()
            userPhone = user.phoneNumber.orEmpty()
            userAvatar = user.photoUrl.toString()
            LifestylesRepository.getLifestyles().subscribe({ styles ->
                _lifestyles.postValue(styles.map { lifestyle -> lifestyle.mapToListLifestyle() })
                _loadingStatus.postValue(Status.Success)
            }, {
                _loadingStatus.postValue(Status.Error)
            })
        } ?: _loadingStatus.postValue(Status.Error)
    }

    fun onLifestyleItemClicked(lifestyleId: String) {
        if (userLifestyles.contains(lifestyleId)) {
            userLifestyles.remove(lifestyleId)
        } else {
            userLifestyles.add(lifestyleId)
        }
        lifestyles.value?.let { lifestyles ->
            _lifestyles.postValue(lifestyles.map { lifestyle ->
                lifestyle.copy(isSelected = userLifestyles.contains(lifestyle.id))
            })
        }
    }

    fun signUp(name: String, nick: String) {
        _userName.postValue(name)
        _userNick.postValue(nick)
        _loadingStatus.postValue(Status.Loading)
        _checkingStatus.postValue(Status.Loading)
        compositeDisposable.add(UsersRepository.getNickExist(nick).subscribe({ status ->
            if (status == UserStatus.NotFound) {
                _checkingStatus.postValue(Status.Success)
                compositeDisposable.add(
                    UsersRepository.addUser(
                        id = userId,
                        name = name,
                        nick = nick,
                        email = userEmail,
                        phone = userPhone,
                        avatar = userAvatar,
                        lifestyles = userLifestyles,
                    ).subscribe({
                        /* do nothing */
                    }, {
                        _loadingStatus.postValue(Status.Error)
                    })
                )
            } else {
                _loadingStatus.postValue(Status.Success)
                _checkingStatus.postValue(Status.Error)
            }
        }, {
            _loadingStatus.postValue(Status.Error)
            _checkingStatus.postValue(Status.Success)
        }))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}