package com.theost.tike.feature.inbox.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.R
import com.theost.tike.common.extension.hideItem
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.common.util.LogUtils
import com.theost.tike.common.util.LogUtils.LOG_VIEW_MODEL_INBOX
import com.theost.tike.core.recycler.event.EventUi
import com.theost.tike.core.recycler.event.mapToEventUi
import com.theost.tike.core.recycler.friend.FriendUi
import com.theost.tike.core.recycler.friend.mapToFriendUi
import com.theost.tike.core.recycler.title.TitleUi
import com.theost.tike.core.recycler.user.mapToUserUi
import com.theost.tike.domain.model.multi.EventMode
import com.theost.tike.domain.model.multi.FriendMode
import com.theost.tike.domain.model.multi.Source.Empty
import com.theost.tike.domain.model.multi.Status
import com.theost.tike.domain.model.multi.Status.*
import com.theost.tike.domain.repository.EventsRepository
import com.theost.tike.domain.repository.FriendsRepository
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.Observable
import io.reactivex.Single
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
                Observable.combineLatest(
                    Observable.concat(
                        Observable.just(listOf(Empty)),
                        UsersRepository.observeUser(firebaseUser.uid).switchMap { databaseUser ->
                            UsersRepository.observeUsers(databaseUser.pending).map { users ->
                                users.map { it.mapToFriendUi(firebaseUser.uid, FriendMode.PENDING) }
                            }
                        }
                    ),
                    Observable.concat(
                        Observable.just(listOf(Empty)),
                        EventsRepository.observeReferencePendingEvents(firebaseUser.uid)
                            .switchMapSingle { events ->
                                Observable.fromIterable(events).flatMapSingle { event ->
                                    UsersRepository.getUser(event.creatorId).map { user ->
                                        event.mapToEventUi(
                                            listOf(user.mapToUserUi(firebaseUser.uid)),
                                            EventMode.PENDING_IN
                                        )
                                    }
                                }.toList()
                            }
                    ),
                    Observable.concat(
                        Observable.just(listOf(Empty)),
                        EventsRepository.observeProperRequestingEvents(firebaseUser.uid)
                            .switchMapSingle { events ->
                                if (events.isNotEmpty()) {
                                    Single.zip(
                                        events.map { event ->
                                            UsersRepository.getUsers(event.requesting)
                                                .map { users ->
                                                    users.map { user ->
                                                        event.mapToEventUi(
                                                            listOf(user.mapToUserUi(firebaseUser.uid)),
                                                            EventMode.REQUESTING_IN
                                                        )
                                                    }
                                                }
                                        }
                                    ) { sources ->
                                        mutableListOf<EventUi>().apply {
                                            sources.filterIsInstance<List<EventUi>>()
                                                .forEach { addAll(it) }
                                        }
                                    }
                                } else {
                                    Single.just(emptyList())
                                }
                            }
                    ),
                    Observable.concat(
                        Observable.just(listOf(Empty)),
                        UsersRepository.observeUser(firebaseUser.uid).switchMap { databaseUser ->
                            UsersRepository.observeUsers(databaseUser.requesting).map { users ->
                                users.map {
                                    it.mapToFriendUi(
                                        firebaseUser.uid,
                                        FriendMode.REQUESTING
                                    )
                                }
                            }
                        }
                    ),
                    Observable.concat(
                        Observable.just(listOf(Empty)),
                        EventsRepository.observeProperPendingEvents(firebaseUser.uid)
                            .switchMapSingle { events ->
                                if (events.isNotEmpty()) {
                                    Single.zip(
                                        events.map { event ->
                                            UsersRepository.getUsers(event.pending).map { users ->
                                                users.map { user ->
                                                    event.mapToEventUi(
                                                        listOf(user.mapToUserUi(firebaseUser.uid)),
                                                        EventMode.PENDING_OUT
                                                    )
                                                }
                                            }
                                        }
                                    ) { sources ->
                                        mutableListOf<EventUi>().apply {
                                            sources.filterIsInstance<List<EventUi>>()
                                                .forEach { addAll(it) }
                                        }
                                    }
                                } else {
                                    Single.just(emptyList())
                                }
                            }
                    ),
                    Observable.concat(
                        Observable.just(listOf(Empty)),
                        EventsRepository.observeReferenceRequestingEvents(firebaseUser.uid)
                            .switchMapSingle { events ->
                                Observable.fromIterable(events).concatMapSingle { event ->
                                    UsersRepository.getUser(event.creatorId).map { user ->
                                        event.mapToEventUi(
                                            listOf(user.mapToUserUi(firebaseUser.uid)),
                                            EventMode.REQUESTING_OUT
                                        )
                                    }
                                }.toList()
                            }
                    )
                ) { pendingFriends, pendingInEvents, requestingInEvents, requestingFriends, pendingOutEvents, requestingOutEvents ->
                    mutableListOf<Any>().apply {
                        if (pendingFriends.isNotEmpty()) {
                            add(TitleUi(R.string.pending_friends))
                            addAll(pendingFriends)
                        }
                        if (pendingInEvents.isNotEmpty()) {
                            add(TitleUi(R.string.pending_in_events))
                            addAll(pendingInEvents)
                        }
                        if (requestingInEvents.isNotEmpty()) {
                            add(TitleUi(R.string.requesting_in_events))
                            addAll(requestingInEvents)
                        }
                        if (requestingFriends.isNotEmpty()) {
                            add(TitleUi(R.string.requesting_friends))
                            addAll(requestingFriends)
                        }
                        if (pendingOutEvents.isNotEmpty()) {
                            add(TitleUi(R.string.pending_out_events))
                            addAll(pendingOutEvents)
                        }
                        if (requestingOutEvents.isNotEmpty()) {
                            add(TitleUi(R.string.requesting_out_events))
                            addAll(requestingOutEvents)
                        }
                    }
                }
            }.subscribe({ items ->
                isListenerAttached = true
                if (!items.contains(Empty).or(items.filterIsInstance<Empty>().isNotEmpty())) {
                    _items.postValue(items.filterIsInstance<DelegateItem>())
                    _loadingStatus.postValue(Success)
                }
            }, { error ->
                isListenerAttached = false
                _items.postValue(emptyList())
                _loadingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_INBOX, error.toString())
            })
        )
    }

    fun addFromRequestingInEvent(id: String, creator: String, requesting: String) {
        _items.hideItem(EventUi::id, id, TitleUi::stringRes, R.string.requesting_in_events)
        compositeDisposable.add(
            EventsRepository.getEvent(creator, id).flatMapCompletable { event ->
                EventsRepository.addReferenceFromRequestingActiveEvent(
                    id,
                    creator,
                    requesting,
                    event.participants
                )
            }.subscribe({
                Log.i(LogUtils.LOG_VIEW_MODEL_PROFILE, "Requesting In Event $id accepted")
            }, { error ->
                Log.e(LogUtils.LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }

    fun addFromPendingInEvent(id: String, creator: String) {
        _items.hideItem(EventUi::id, id, TitleUi::stringRes, R.string.pending_in_events)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                EventsRepository.getEvent(creator, id).flatMapCompletable { event ->
                    EventsRepository.addReferenceFromPendingActiveEvent(
                        id,
                        creator,
                        firebaseUser.uid,
                        event.participants
                    )
                }
            }.subscribe({
                Log.i(LogUtils.LOG_VIEW_MODEL_PROFILE, "Pending In Event $id accepted")
            }, { error ->
                Log.e(LogUtils.LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }

    fun deleteRequestingInEvent(id: String, requesting: String) {
        _items.hideItem(EventUi::id, id, TitleUi::stringRes, R.string.requesting_in_events)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                EventsRepository.deleteReferenceRequestingEvent(id, firebaseUser.uid, requesting)
            }.subscribe({
                Log.i(LogUtils.LOG_VIEW_MODEL_PROFILE, "Requesting In Event $id deleted")
            }, { error ->
                Log.e(LogUtils.LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }

    fun deleteRequestingOutEvent(id: String, creator: String) {
        _items.hideItem(EventUi::id, id, TitleUi::stringRes, R.string.requesting_out_events)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                EventsRepository.deleteReferenceRequestingEvent(id, creator, firebaseUser.uid)
            }.subscribe({
                Log.i(LogUtils.LOG_VIEW_MODEL_PROFILE, "Requesting Out Event $id deleted")
            }, { error ->
                Log.e(LogUtils.LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }

    fun deletePendingInEvent(id: String, creator: String) {
        _items.hideItem(EventUi::id, id, TitleUi::stringRes, R.string.pending_in_events)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                EventsRepository.getEvent(creator, id).flatMapCompletable { event ->
                    EventsRepository.deleteReferencePendingEvent(
                        id,
                        creator,
                        firebaseUser.uid,
                        event.participantsLimit
                    )
                }
            }.subscribe({
                Log.i(LogUtils.LOG_VIEW_MODEL_PROFILE, "Pending In Event $id deleted")
            }, { error ->
                Log.e(LogUtils.LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }

    fun deletePendingOutEvent(id: String, pending: String) {
        _items.hideItem(EventUi::id, id, TitleUi::stringRes, R.string.pending_out_events)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                EventsRepository.getEvent(firebaseUser.uid, id).flatMapCompletable { event ->
                    EventsRepository.deleteReferencePendingEvent(
                        id,
                        firebaseUser.uid,
                        pending,
                        event.participantsLimit
                    )
                }
            }.subscribe({
                Log.i(LogUtils.LOG_VIEW_MODEL_PROFILE, "Pending Out Event $id deleted")
            }, { error ->
                Log.e(LogUtils.LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }

    fun addFriend(uid: String) {
        _items.hideItem(FriendUi::uid, uid, TitleUi::stringRes, R.string.pending_friends)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                UsersRepository.getUser(firebaseUser.uid).flatMapCompletable { requestingUser ->
                    UsersRepository.getUser(uid).flatMapCompletable { requestedUser ->
                        FriendsRepository.addFriend(
                            requestingUser.uid,
                            requestedUser.uid,
                            requestingUser.friends,
                            requestedUser.friends
                        )
                    }
                }
            }.subscribe({
                Log.i(LogUtils.LOG_VIEW_MODEL_PROFILE, "User $uid added to friends")
            }, { error ->
                Log.e(LogUtils.LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }

    fun deleteFriendRequest(uid: String) {
        _items.hideItem(FriendUi::uid, uid, TitleUi::stringRes, R.string.pending_friends)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { requestingUser ->
                FriendsRepository.deleteFriendRequest(uid, requestingUser.uid)
            }.subscribe({
                Log.i(LogUtils.LOG_VIEW_MODEL_PROFILE, "Friend request from $uid deleted")
            }, { error ->
                Log.e(LogUtils.LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }

    fun blockUser(uid: String) {
        _items.hideItem(FriendUi::uid, uid, TitleUi::stringRes, R.string.pending_friends)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
                UsersRepository.getUser(firebaseUser.uid).flatMapCompletable { requestingUser ->
                    FriendsRepository.blockUser(
                        requestingUser.uid,
                        uid,
                        requestingUser.blocked
                    )
                }
            }.subscribe({
                Log.i(LogUtils.LOG_VIEW_MODEL_PROFILE, "User $uid blocked")
            }, { error ->
                Log.e(LogUtils.LOG_VIEW_MODEL_PROFILE, error.toString())
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
