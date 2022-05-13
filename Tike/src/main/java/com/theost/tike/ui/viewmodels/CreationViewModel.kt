package com.theost.tike.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.data.models.state.RepeatMode
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.state.Status.Loading
import com.theost.tike.data.models.state.Status.Success
import com.theost.tike.data.models.state.Status.Error
import com.theost.tike.data.models.ui.UserUi
import com.theost.tike.data.models.ui.mapToUserUi
import com.theost.tike.data.repositories.EventsRepository
import com.theost.tike.data.repositories.UsersRepository
import com.theost.tike.ui.extensions.append
import com.theost.tike.ui.extensions.isNotLower
import com.theost.tike.ui.utils.LogUtils.LOG_VIEW_MODEL_CREATION
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

class CreationViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _eventDate = MutableLiveData<LocalDate>()
    val eventDate: LiveData<LocalDate> = _eventDate

    private val _eventBeginTime = MutableLiveData<LocalTime>()
    val eventBeginTime: LiveData<LocalTime> = _eventBeginTime

    private val _eventEndTime = MutableLiveData<LocalTime>()
    val eventEndTime: LiveData<LocalTime> = _eventEndTime

    private val _participantsLimit = MutableLiveData<Int>()
    val participantsLimit: LiveData<Int> = _participantsLimit

    private val _participants = MutableLiveData<List<UserUi>>()
    val participants: LiveData<List<UserUi>> = _participants

    private var participantsIds = emptyList<String>()
    private val compositeDisposable = CompositeDisposable()

    init {
        val eventDateTime = LocalDateTime.now().plusHours(DATE_EVENT_DEFAULT_AFTER)
        val eventDate = eventDateTime.toLocalDate()
        val eventBeginTime = LocalTime.of(eventDateTime.hour, 0)
        val eventEndTime = eventBeginTime.plusHours(DATE_EVENT_DEFAULT_LENGTH)

        _eventDate.value = eventDate
        _eventBeginTime.value = eventBeginTime
        _eventEndTime.value = eventEndTime
        _participantsLimit.value = 0
        _participants.value = emptyList()
    }

    fun sendEventData(title: String, description: String, repeatMode: RepeatMode) {
        _loadingStatus.postValue(Loading)

        val weekDay = eventDate.value?.dayOfWeek?.value ?: 0
        val monthDay = eventDate.value?.dayOfMonth ?: 0
        val month = eventDate.value?.monthValue ?: 0
        val year = eventDate.value?.year ?: 0
        val beginTime = eventBeginTime.value?.toNanoOfDay() ?: 0
        val endTime = eventEndTime.value?.toNanoOfDay() ?: 0
        val participantsLimit = participantsLimit.value ?: 0
        val participants = participants.value.orEmpty().map { user -> user.uid }

        val creationDate = LocalDateTime.now().nano

        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).toSingle()
                .flatMapCompletable { firebaseUser ->
                    EventsRepository.addEvent(
                        uid = firebaseUser.uid,
                        title = title,
                        description = description,
                        participants = participants.append(firebaseUser.uid),
                        participantsLimit = participantsLimit.plus(1),
                        created = creationDate,
                        modified = creationDate,
                        weekDay = weekDay,
                        monthDay = monthDay,
                        month = month,
                        year = year,
                        beginTime = beginTime,
                        endTime = endTime,
                        repeatMode = repeatMode.name
                    )
                }.subscribe({
                    _loadingStatus.postValue(Success)
                }, { error ->
                    _loadingStatus.postValue(Error)
                    Log.e(LOG_VIEW_MODEL_CREATION, error.toString())
                })
        )
    }

    fun updateEventDate(year: Int, month: Int, day: Int) {
        _eventDate.postValue(LocalDate.of(year, month, day))
    }

    fun updateEventBeginTime(hour: Int, minute: Int) {
        _eventBeginTime.postValue(LocalTime.of(hour, minute))
        _eventEndTime.postValue(LocalTime.of(hour, minute).plusHours(DATE_EVENT_DEFAULT_LENGTH))
    }

    fun updateEventEndTime(hour: Int, minute: Int) {
        eventBeginTime.value?.let { beginTime ->
            val endTime = LocalTime.of(hour, minute)
            if (endTime.isAfter(beginTime)) _eventEndTime.postValue(endTime)
        }
    }

    fun updateParticipantsLimit(value: Int) {
        participantsLimit.value?.let { cachedValue ->
            cachedValue.plus(value).let { limit ->
                _participantsLimit.value =
                    if (limit.isNotLower(participantsIds.size)) limit else cachedValue
            }
        }
    }

    fun loadParticipants(usersIds: List<String> = emptyList()) {
        if (usersIds != participantsIds) {
            participantsIds = usersIds.toList()
            compositeDisposable.add(
                RxFirebaseAuth.getCurrentUser(Firebase.auth).toSingle().flatMap { firebaseUser ->
                    UsersRepository.getUsers(participantsIds).map { users ->
                        users.map { user -> user.mapToUserUi(firebaseUser.uid) }
                            .distinctBy { participant -> participant.uid }
                    }
                }.subscribe({ users ->
                    _participantsLimit.postValue(users.size)
                    _participants.postValue(users)
                }, { error ->
                    Log.e(LOG_VIEW_MODEL_CREATION, error.toString())
                })
            )
        }
    }

    fun removeParticipant(uid: String) {
        participants.value?.let { items ->
            val participantsCount = participantsIds.size
            participantsIds = participantsIds.filterNot { id -> id == uid }
            updateParticipantsLimit(participantsIds.size - participantsCount)
            _participants.postValue(
                items.toMutableList()
                    .filterNot { participant -> participant.uid == uid }
                    .toList()
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    companion object {

        private const val DATE_EVENT_DEFAULT_AFTER = 1L
        private const val DATE_EVENT_DEFAULT_LENGTH = 1L
    }
}
