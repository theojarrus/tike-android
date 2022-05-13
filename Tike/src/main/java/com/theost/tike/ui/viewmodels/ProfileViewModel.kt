package com.theost.tike.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.state.Status.*
import com.theost.tike.data.models.ui.ProfileUi
import com.theost.tike.data.models.ui.UserUi
import com.theost.tike.data.models.ui.mapToProfileUi
import com.theost.tike.data.models.ui.mapToUserUi
import com.theost.tike.data.repositories.UsersRepository
import com.theost.tike.ui.utils.LogUtils.LOG_VIEW_MODEL_PROFILE
import io.reactivex.disposables.CompositeDisposable

class ProfileViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _user = MutableLiveData<ProfileUi>()
    val user: LiveData<ProfileUi> = _user

    private val compositeDisposable = CompositeDisposable()

    fun init(uid: String) {
        if (user.value == null) loadUser(uid)
    }

    private fun loadUser(uid: String) {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).toSingle().flatMap { firebaseUser ->
                UsersRepository.getUser(firebaseUser.uid)
            }.flatMap { currentUser ->
                UsersRepository.getUser(uid).map { profileUser ->
                    profileUser.mapToProfileUi(
                        currentUid = currentUser.uid,
                        currentUserBlocked = currentUser.blocked,
                        profileUserBlocked = profileUser.blocked
                    )
                }
            }.subscribe({ user ->
                _user.postValue(user)
                _loadingStatus.postValue(Success)
            }, { error ->
                _loadingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }
}
