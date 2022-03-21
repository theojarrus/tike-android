package com.theost.tike.ui.viewmodels

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.data.extensions.RxSearchObservable
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.ui.ListUser
import com.theost.tike.data.models.ui.mapToListUser
import com.theost.tike.data.repositories.UsersRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ParticipantsViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _participants = MutableLiveData<List<ListUser>>()
    val participants: LiveData<List<ListUser>> = _participants

    private val _selectedIds = MutableLiveData<List<String>>()
    val selectedIds: LiveData<List<String>> = _selectedIds

    private var participantsCache = emptyList<ListUser>()
    private val compositeDisposable = CompositeDisposable()

    fun loadUsers(addedIds: List<String>) {
        if (participantsCache.isEmpty()) {
            Firebase.auth.currentUser?.uid?.let { userId ->
                _loadingStatus.postValue(Status.Loading)
                compositeDisposable.add(UsersRepository.getUser(userId).flatMap { user ->
                    UsersRepository.getUsers(user.friends, user.blocked)
                }.subscribe({ users ->
                    participantsCache = users.map { user -> user.mapToListUser(addedIds) }
                    _selectedIds.postValue(addedIds.filter { id -> users.map { user -> user.id }.contains(id) })
                    _participants.postValue(participantsCache)
                    _loadingStatus.postValue(Status.Success)
                }, {
                    _loadingStatus.postValue(Status.Error) }))
            }
        }
    }

    fun setupSearch(searchView: SearchView) {
        compositeDisposable.add(RxSearchObservable.fromView(searchView).subscribeOn(Schedulers.io())
            .map { query -> query.trim() }
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS)
            .switchMapSingle { query ->
                if (query.isNotEmpty()) {
                    Observable.fromIterable(participantsCache).filter { user ->
                        user.name.lowercase().contains(query) || user.nick.lowercase()
                            .contains(query)
                    }.toList()
                } else {
                    Single.just(participantsCache)
                }
            }
            .subscribe(
                { users -> _participants.postValue(users) },
                { error -> error.printStackTrace() }
            )
        )
    }

    fun onParticipantItemClicked(userId: String, isSelected: Boolean) {
        compositeDisposable.add(Single.fromCallable {
            val ids = selectedIds.value.orEmpty().toMutableList()
            val users = participants.value.orEmpty()

            if (isSelected) ids.add(userId) else ids.remove(userId)
            val items = users.map { user -> user.copy(isSelected = ids.contains(user.id)) }
            participantsCache =
                participantsCache.map { user -> user.copy(isSelected = ids.contains(user.id)) }

            _selectedIds.postValue(ids.toList())
            _participants.postValue(items)
        }.subscribeOn(Schedulers.computation()).subscribe())
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}