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
import com.theost.tike.data.models.ui.mapToProfileUi
import com.theost.tike.data.repositories.PeopleRepository
import com.theost.tike.data.repositories.UsersRepository
import com.theost.tike.ui.utils.LogUtils.LOG_VIEW_MODEL_PROFILE
import io.reactivex.disposables.CompositeDisposable

class ProfileViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _updatingStatus = MutableLiveData<Status>()
    val updatingStatus: LiveData<Status> = _updatingStatus

    private val _user = MutableLiveData<ProfileUi>()
    val user: LiveData<ProfileUi> = _user

    private val compositeDisposable = CompositeDisposable()

    fun init(uid: String) {
        if (user.value == null) loadUser(uid) else reloadUser()
    }

    private fun reloadUser() {
        user.value?.uid?.let { uid -> loadUser(uid, true) }
    }

    private fun loadUser(uid: String, isFirstLoad: Boolean = false) {
        if (isFirstLoad) _loadingStatus.postValue(Loading)
        (if (isFirstLoad) _loadingStatus else _updatingStatus).postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapSingle { firebaseUser ->
                UsersRepository.getUser(firebaseUser.uid)
            }.flatMap { databaseUser ->
                UsersRepository.getUser(uid).map { it.mapToProfileUi(databaseUser) }
            }.subscribe({ user ->
                _user.postValue(user)
                updateLoadingStatus(Success, isFirstLoad)
            }, { error ->
                updateLoadingStatus(Error, isFirstLoad)
                Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }

    fun cancelPending() {
        _updatingStatus.postValue(Loading)
        user.value?.uid?.let { requestedUid ->
            compositeDisposable.add(
                RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { requestingUser ->
                    PeopleRepository.deleteFriendRequest(requestingUser.uid, requestedUid)
                }.subscribe({
                    reloadUser()
                }, { error ->
                    _updatingStatus.postValue(Error)
                    Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
                })
            )
        }
    }

    fun addFriend() {
        _updatingStatus.postValue(Loading)
        user.value?.uid?.let { requestedUid ->
            compositeDisposable.add(
                RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                    UsersRepository.getUser(firebaseUser.uid).flatMapCompletable { requestingUser ->
                        UsersRepository.getUser(requestedUid).flatMapCompletable { requestedUser ->
                            PeopleRepository.addFriend(
                                requestingUser.uid,
                                requestedUser.uid,
                                requestingUser.friends,
                                requestedUser.friends
                            )
                        }
                    }
                }.subscribe({
                    reloadUser()
                }, { error ->
                    _updatingStatus.postValue(Error)
                    Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
                })
            )
        }
    }

    fun deleteFriend() {
        _updatingStatus.postValue(Loading)
        user.value?.uid?.let { requestedUid ->
            compositeDisposable.add(
                RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                    UsersRepository.getUser(firebaseUser.uid).flatMapCompletable { requestingUser ->
                        PeopleRepository.deleteFriend(
                            requestingUser.uid,
                            requestedUid,
                            requestingUser.pending
                        )
                    }
                }.subscribe({
                    reloadUser()
                }, { error ->
                    _updatingStatus.postValue(Error)
                    Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
                })
            )
        }
    }

    fun addFriendRequest() {
        _updatingStatus.postValue(Loading)
        user.value?.uid?.let { requestedUid ->
            compositeDisposable.add(
                RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { requestingUser ->
                    UsersRepository.getUser(requestedUid).flatMapCompletable { requestedUser ->
                        PeopleRepository.addFriendRequest(
                            requestingUser.uid,
                            requestedUid,
                            requestedUser.pending
                        )
                    }
                }.subscribe({
                    reloadUser()
                }, { error ->
                    _updatingStatus.postValue(Error)
                    Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
                })
            )
        }
    }

    fun blockUser() {
        _updatingStatus.postValue(Loading)
        user.value?.uid?.let { requestedUid ->
            compositeDisposable.add(
                RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                    UsersRepository.getUser(firebaseUser.uid).flatMapCompletable { requestingUser ->
                        PeopleRepository.blockUser(
                            requestingUser.uid,
                            requestedUid,
                            requestingUser.blocked
                        )
                    }
                }.subscribe({
                    reloadUser()
                }, { error ->
                    _updatingStatus.postValue(Error)
                    Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
                })
            )
        }
    }

    fun unblockUser() {
        _updatingStatus.postValue(Loading)
        user.value?.uid?.let { requestedUid ->
            compositeDisposable.add(
                RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { requestingUser ->
                    PeopleRepository.unblockUser(requestingUser.uid, requestedUid)
                }.subscribe({
                    reloadUser()
                }, { error ->
                    _updatingStatus.postValue(Error)
                    Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
                })
            )
        }
    }

    private fun updateLoadingStatus(status: Status, isFirstLoad: Boolean) {
        if (isFirstLoad) _loadingStatus.postValue(status) else _updatingStatus.postValue(status)
    }
}
