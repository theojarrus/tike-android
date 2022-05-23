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
import com.theost.tike.data.repositories.FriendsRepository
import com.theost.tike.data.repositories.UsersRepository
import com.theost.tike.ui.utils.LogUtils.LOG_VIEW_MODEL_PROFILE
import io.reactivex.disposables.CompositeDisposable

class ProfileViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _user = MutableLiveData<ProfileUi>()
    val user: LiveData<ProfileUi> = _user

    private var isListenerAttached = false
    private val compositeDisposable = CompositeDisposable()

    fun init(uid: String) {
        if (!isListenerAttached) loadUser(uid, isFirstLoad = true)
    }

    private fun reloadUser() {
        user.value?.uid?.let { uid ->
            compositeDisposable.clear()
            loadUser(uid, isFirstLoad = false)
        }
    }

    private fun loadUser(uid: String, isFirstLoad: Boolean) {
        if (isFirstLoad) _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapObservable { firebaseUser ->
                UsersRepository.observeUser(firebaseUser.uid)
            }.flatMap { databaseUser ->
                UsersRepository.observeUser(uid).map { it.mapToProfileUi(databaseUser) }
            }.subscribe({ user ->
                isListenerAttached = true
                _user.postValue(user)
                _loadingStatus.postValue(Success)
            }, { error ->
                isListenerAttached = false
                _loadingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }

    fun cancelPending() {
        compositeDisposable.clear()
        _loadingStatus.postValue(Loading)
        user.value?.uid?.let { requestedUid ->
            compositeDisposable.add(
                RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { requestingUser ->
                    FriendsRepository.deleteFriendRequest(requestingUser.uid, requestedUid)
                }.subscribe({
                    reloadUser()
                }, { error ->
                    _loadingStatus.postValue(Error)
                    Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
                })
            )
        }
    }

    fun addFriend() {
        compositeDisposable.clear()
        _loadingStatus.postValue(Loading)
        user.value?.uid?.let { requestedUid ->
            compositeDisposable.add(
                RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                    UsersRepository.getUser(firebaseUser.uid).flatMapCompletable { requestingUser ->
                        UsersRepository.getUser(requestedUid).flatMapCompletable { requestedUser ->
                            FriendsRepository.addFriend(
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
                    _loadingStatus.postValue(Error)
                    Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
                })
            )
        }
    }

    fun deleteFriend() {
        compositeDisposable.clear()
        _loadingStatus.postValue(Loading)
        user.value?.uid?.let { requestedUid ->
            compositeDisposable.add(
                RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                    UsersRepository.getUser(firebaseUser.uid).flatMapCompletable { requestingUser ->
                        UsersRepository.getUser(requestedUid).flatMapCompletable { requestedUser ->
                            FriendsRepository.deleteFriend(
                                requestingUser.uid,
                                requestedUid,
                                requestingUser.pending,
                                requestedUser.requesting
                            )
                        }
                    }
                }.subscribe({
                    reloadUser()
                }, { error ->
                    _loadingStatus.postValue(Error)
                    Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
                })
            )
        }
    }

    fun addFriendRequest() {
        compositeDisposable.clear()
        _loadingStatus.postValue(Loading)
        user.value?.uid?.let { requestedUid ->
            compositeDisposable.add(
                RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                    UsersRepository.getUser(firebaseUser.uid).flatMapCompletable { requestingUser ->
                        UsersRepository.getUser(requestedUid).flatMapCompletable { requestedUser ->
                            FriendsRepository.addFriendRequest(
                                requestingUser.uid,
                                requestedUid,
                                requestingUser.requesting,
                                requestedUser.pending
                            )
                        }
                    }
                }.subscribe({
                    reloadUser()
                }, { error ->
                    _loadingStatus.postValue(Error)
                    Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
                })
            )
        }
    }

    fun blockUser() {
        compositeDisposable.clear()
        _loadingStatus.postValue(Loading)
        user.value?.uid?.let { requestedUid ->
            compositeDisposable.add(
                RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                    UsersRepository.getUser(firebaseUser.uid).flatMapCompletable { requestingUser ->
                        FriendsRepository.blockUser(
                            requestingUser.uid,
                            requestedUid,
                            requestingUser.blocked
                        )
                    }
                }.subscribe({
                    reloadUser()
                }, { error ->
                    _loadingStatus.postValue(Error)
                    Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
                })
            )
        }
    }

    fun unblockUser() {
        compositeDisposable.clear()
        _loadingStatus.postValue(Loading)
        user.value?.uid?.let { requestedUid ->
            compositeDisposable.add(
                RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { requestingUser ->
                    FriendsRepository.unblockUser(requestingUser.uid, requestedUid)
                }.subscribe({
                    reloadUser()
                }, { error ->
                    _loadingStatus.postValue(Error)
                    Log.e(LOG_VIEW_MODEL_PROFILE, error.toString())
                })
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
