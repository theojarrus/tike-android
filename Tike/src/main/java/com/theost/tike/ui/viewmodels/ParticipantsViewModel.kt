package com.theost.tike.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.state.Status.Loading
import com.theost.tike.data.models.state.Status.Success
import com.theost.tike.data.models.state.Status.Error
import com.theost.tike.data.models.ui.ParticipantUi
import com.theost.tike.data.models.ui.mapToParticipantUi
import com.theost.tike.data.repositories.UsersRepository
import com.theost.tike.ui.utils.LogUtils.LOG_VIEW_MODEL_PARTICIPANTS
import io.reactivex.disposables.CompositeDisposable

class ParticipantsViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _participants = MutableLiveData<List<ParticipantUi>>()
    val participants: LiveData<List<ParticipantUi>> = _participants

    private val _selectedParticipants = MutableLiveData<List<String>>()
    val selectedParticipants: LiveData<List<String>> = _selectedParticipants

    private var cachedParticipants = emptyList<ParticipantUi>()
    private val compositeDisposable = CompositeDisposable()

    fun init(participants: List<String>) {
        if (selectedParticipants.value == null) {
            _selectedParticipants.value = participants
            loadUsers()
        }
    }

    fun searchParticipants(query: String) {
        _participants.postValue(
            cachedParticipants.filter {
                it.name.lowercase().contains(query)
                    .or(it.nick.lowercase().contains(query))
            }
        )
    }

    private fun loadUsers() {
        if (cachedParticipants.isEmpty()) {
            _loadingStatus.postValue(Loading)
            compositeDisposable.add(
                RxFirebaseAuth.getCurrentUser(Firebase.auth).toSingle().flatMap { firebaseUser ->
                    UsersRepository.getUser(firebaseUser.uid).flatMap { databaseUser ->
                        UsersRepository.getUsers(databaseUser.friends, databaseUser.blocked).map {
                            it.map { user -> user.mapToParticipantUi(firebaseUser.uid) }
                                .filter { user -> user.hasAccess }
                        }
                    }
                }.subscribe({ users ->
                    cachedParticipants = users
                    _participants.postValue(users.mapWithSelection())
                    _loadingStatus.postValue(Success)
                }, { error ->
                    _loadingStatus.postValue(Error)
                    Log.e(LOG_VIEW_MODEL_PARTICIPANTS, error.toString())
                })
            )
        }
    }

    fun selectParticipant(uid: String) {
        val selectedIds = selectedParticipants.value.orEmpty().toMutableList()
            .apply { if (contains(uid)) remove(uid) else add(uid) }
        val items = participants.value.orEmpty()
            .map { user -> user.copy(isSelected = selectedIds.contains(user.uid)) }
        _selectedParticipants.postValue(selectedIds)
        _participants.postValue(items)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    private fun List<ParticipantUi>.mapWithSelection(): List<ParticipantUi> = map { user ->
        user.copy(isSelected = selectedParticipants.value.orEmpty().contains(user.uid))
    }
}
