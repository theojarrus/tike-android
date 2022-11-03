package com.theost.tike.feature.members.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.common.recycler.element.member.MemberUi
import com.theost.tike.common.recycler.element.member.mapToParticipantUi
import com.theost.tike.common.util.LogUtils.LOG_VIEW_MODEL_PARTICIPANTS
import com.theost.tike.domain.model.multi.Status
import com.theost.tike.domain.model.multi.Status.*
import com.theost.tike.domain.repository.UsersRepository
import io.reactivex.disposables.CompositeDisposable

class ParticipantsViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _participants = MutableLiveData<List<MemberUi>>()
    val participants: LiveData<List<MemberUi>> = _participants

    private val _selectedIds = MutableLiveData<List<String>>()
    val selectedIds: LiveData<List<String>> = _selectedIds

    private var cachedParticipants = emptyList<MemberUi>()
    private var cachedSearchQuery = ""
    private var isListenerAttached = false

    private val compositeDisposable = CompositeDisposable()

    fun init(participants: List<String>) {
        if (selectedIds.value == null) _selectedIds.value = participants
        if (!isListenerAttached) loadUsers()
    }

    private fun restoreState(users: List<MemberUi>) {
        cachedParticipants = users
        selectedIds.value?.let { selected ->
            _selectedIds.postValue(selected.filter { id -> users.map { it.uid }.contains(id) })
        }
        if (cachedSearchQuery.isNotEmpty()) {
            searchParticipants(cachedSearchQuery)
        } else {
            _participants.postValue(users.mapWithSelection())
        }
    }

    fun searchParticipants(query: String) {
        cachedSearchQuery = query
        if (query.isNotEmpty()) {
            _participants.postValue(
                cachedParticipants.filter {
                    it.name.orEmpty().lowercase().contains(query)
                        .or(it.nick.orEmpty().lowercase().contains(query))
                }.mapWithSelection()
            )
        } else {
            _participants.postValue(cachedParticipants.mapWithSelection())
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
        val selectedIds = selectedIds.value.orEmpty().toMutableList()
            .apply { if (contains(uid)) remove(uid) else add(uid) }
        val items = participants.value.orEmpty()
            .map { it.copy(isSelected = selectedIds.contains(it.uid)) }
        _selectedIds.postValue(selectedIds)
        _participants.postValue(items)
    }

    private fun List<MemberUi>.mapWithSelection(): List<MemberUi> = map { user ->
        user.copy(isSelected = selectedIds.value.orEmpty().contains(user.uid))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
