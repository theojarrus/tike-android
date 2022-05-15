package com.theost.tike.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.state.Status.Error
import com.theost.tike.data.models.state.Status.Loading
import com.theost.tike.data.models.state.Status.Success
import com.theost.tike.data.models.ui.TitleUi
import com.theost.tike.data.models.ui.mapToUserUi
import com.theost.tike.data.repositories.PeopleRepository
import com.theost.tike.data.repositories.UsersRepository
import com.theost.tike.ui.interfaces.DelegateItem
import com.theost.tike.ui.utils.LogUtils
import com.theost.tike.ui.utils.LogUtils.LOG_VIEW_MODEL_INBOX
import io.reactivex.disposables.CompositeDisposable

class InboxViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _items = MutableLiveData<List<DelegateItem>>()
    val items: LiveData<List<DelegateItem>> = _items

    private var isListenerAttached = false
    private val compositeDisposable = CompositeDisposable()

    fun init() {
        if (!isListenerAttached) loadInbox()
    }

    private fun loadInbox() {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapObservable { firebaseUser ->
                UsersRepository.observeUser(firebaseUser.uid).switchMap { databaseUser ->
                    UsersRepository.observeUsers(databaseUser.pending).map { users ->
                        users.map { it.mapToUserUi(firebaseUser.uid) }
                    }
                }
            }.subscribe({ users ->
                isListenerAttached = true

                val items = mutableListOf<DelegateItem>()
                items.add(TitleUi("Входящие в друзья"))
                items.addAll(users)
                items.add(TitleUi("Входящие события"))
                items.add(TitleUi("Исходящие события"))

                _items.postValue(items)
                _loadingStatus.postValue(Success)
            }, { error ->
                isListenerAttached = false
                Log.e(LOG_VIEW_MODEL_INBOX, error.toString())
                _loadingStatus.postValue(Error)
            })
        )
    }

    fun addFriend(uid: String) {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                UsersRepository.getUser(firebaseUser.uid).flatMapCompletable { requestingUser ->
                    UsersRepository.getUser(uid).flatMapCompletable { requestedUser ->
                        PeopleRepository.addFriend(
                            requestingUser.uid,
                            requestedUser.uid,
                            requestingUser.friends,
                            requestedUser.friends
                        )
                    }
                }
            }.subscribe({
                _loadingStatus.postValue(Success)
            }, { error ->
                _loadingStatus.postValue(Error)
                Log.e(LogUtils.LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }

    fun deleteFriendRequest(uid: String) {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { requestingUser ->
                PeopleRepository.deleteFriendRequest(uid, requestingUser.uid)
            }.subscribe({
                _loadingStatus.postValue(Success)
            }, { error ->
                _loadingStatus.postValue(Error)
                Log.e(LogUtils.LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }

    fun blockUser(uid: String) {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                UsersRepository.getUser(firebaseUser.uid).flatMapCompletable { requestingUser ->
                    PeopleRepository.blockUser(
                        requestingUser.uid,
                        uid,
                        requestingUser.blocked
                    )
                }
            }.subscribe({
                _loadingStatus.postValue(Success)
            }, { error ->
                _loadingStatus.postValue(Error)
                Log.e(LogUtils.LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }
}