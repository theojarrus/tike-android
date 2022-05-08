package com.theost.tike.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.state.Status.Error
import com.theost.tike.data.models.ui.UserUi
import com.theost.tike.data.models.ui.mapToUserUi
import com.theost.tike.data.repositories.UsersRepository
import com.theost.tike.ui.utils.LogUtils.LOG_VIEW_MODEL_PARTICIPANTS
import com.theost.tike.ui.widgets.SearchStateViewModel
import io.reactivex.disposables.CompositeDisposable

class ParticipantsViewModel : SearchStateViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _participants = MutableLiveData<List<UserUi>>()
    val participants: LiveData<List<UserUi>> = _participants

    private val _selectedParticipants = MutableLiveData<List<String>>()
    val selectedParticipants: LiveData<List<String>> = _selectedParticipants

    private var cachedParticipants = emptyList<UserUi>()
    private val compositeDisposable = CompositeDisposable()

    override fun bindSearchData(): List<Any> = cachedParticipants

    override fun bindSearchFilter(): (Any, String) -> Boolean = { item, query ->
        (item as? UserUi)?.let { user ->
            user.name.lowercase().contains(query)
                .or(user.nick.lowercase().contains(query))
        } ?: false
    }

    override fun onDataLoaded(items: List<Any>) {
        items.filterIsInstance<UserUi>().let { _participants.postValue(it.mapWithSelection()) }
    }

    override fun onDataLoadError(error: Throwable) {
        Log.e(LOG_VIEW_MODEL_PARTICIPANTS, error.toString())
    }

    fun init(participants: List<String>) {
        if (selectedParticipants.value == null) {
            _selectedParticipants.value = participants
            loadUsers()
        }
    }

    fun loadUsers() {
        if (cachedParticipants.isEmpty()) {
            compositeDisposable.add(
                RxFirebaseAuth.getCurrentUser(Firebase.auth).toSingle().flatMap { firebaseUser ->
                    UsersRepository.getUser(firebaseUser.uid).flatMap { databaseUser ->
                        UsersRepository.getUsers(databaseUser.friends, databaseUser.blocked)
                    }
                }.subscribe({ users ->
                    cachedParticipants = users.map { user -> user.mapToUserUi() }
                    _participants.postValue(cachedParticipants.mapWithSelection())
                    _loadingStatus.postValue(Status.Success)
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
            .map { user -> user.copy(isSelected = selectedIds.contains(user.id)) }
        _selectedParticipants.postValue(selectedIds)
        _participants.postValue(items)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    private fun List<UserUi>.mapWithSelection(): List<UserUi> = map { user ->
        user.copy(isSelected = selectedParticipants.value.orEmpty().contains(user.id))
    }
}