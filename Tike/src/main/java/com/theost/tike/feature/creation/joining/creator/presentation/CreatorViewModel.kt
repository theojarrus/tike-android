package com.theost.tike.feature.creation.joining.creator.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.common.util.LogUtils.LOG_VIEW_MODEL_PARTICIPANTS
import com.theost.tike.core.recycler.member.ParticipantUi
import com.theost.tike.core.recycler.member.mapToParticipantUi
import com.theost.tike.domain.model.multi.Status
import com.theost.tike.domain.model.multi.Status.*
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.disposables.CompositeDisposable

class CreatorViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _participants = MutableLiveData<List<ParticipantUi>>()
    val participants: LiveData<List<ParticipantUi>> = _participants

    private val _selectedId = MutableLiveData<String?>()
    val selectedId: LiveData<String?> = _selectedId

    private var cachedParticipants = emptyList<ParticipantUi>()
    private var cachedSearchQuery = ""
    private var isListenerAttached = false

    private val compositeDisposable = CompositeDisposable()

    fun init(creator: String?) {
        if (selectedId.value == null) _selectedId.value = creator
        if (!isListenerAttached) loadUsers()
    }

    private fun restoreState(users: List<ParticipantUi>) {
        cachedParticipants = users
        selectedId.value?.let { selected ->
            _selectedId.postValue(if (users.map { it.uid }.contains(selected)) selected else null)
        }
        if (cachedSearchQuery.isNotEmpty()) {
            searchParticipants(cachedSearchQuery)
        } else {
            _participants.postValue(users.mapWithSelection(selectedId.value))
        }
    }

    fun searchParticipants(query: String) {
        cachedSearchQuery = query
        if (query.isNotEmpty()) {
            _participants.postValue(
                cachedParticipants.filter {
                    it.name.lowercase().contains(query)
                        .or(it.nick.lowercase().contains(query))
                }.mapWithSelection(selectedId.value)
            )
        } else {
            _participants.postValue(cachedParticipants.mapWithSelection(selectedId.value))
        }
    }

    private fun loadUsers() {
        _loadingStatus.postValue(Loading)
        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapObservable { firebaseUser ->
                UsersRepository.observeUser(firebaseUser.uid).switchMap { databaseUser ->
                    UsersRepository.observeUsers(databaseUser.friends).map { users ->
                        users.map { it.mapToParticipantUi(firebaseUser.uid) }
                            .filter { !databaseUser.blocked.contains(it.uid) }
                            .filter { it.hasAccess }
                    }
                }
            }.subscribe({ users ->
                restoreState(users)
                _loadingStatus.postValue(Success)
            }, { error ->
                _loadingStatus.postValue(Error)
                Log.e(LOG_VIEW_MODEL_PARTICIPANTS, error.toString())
            })
        )
    }

    fun selectParticipant(uid: String) {
        _selectedId.postValue(uid)
        _participants.postValue(participants.value.orEmpty().mapWithSelection(uid))
    }

    private fun List<ParticipantUi>.mapWithSelection(
        selected: String?
    ): List<ParticipantUi> = map { user ->
        user.copy(isSelected = user.uid == selected)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
