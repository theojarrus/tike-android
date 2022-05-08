package com.theost.tike.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.data.models.state.RepeatMode
import com.theost.tike.data.models.ui.ParticipantUi
import com.theost.tike.data.repositories.EventsRepository
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.ui.mapToParticipantUi
import com.theost.tike.data.repositories.UsersRepository
import com.theost.tike.ui.extensions.isPositive
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

    private val _participants = MutableLiveData<List<ParticipantUi>>()
    val participants: LiveData<List<ParticipantUi>> = _participants

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
        _loadingStatus.postValue(Status.Loading)

        val weekDay = eventDate.value?.dayOfWeek?.value ?: 0
        val monthDay = eventDate.value?.dayOfMonth ?: 0
        val month = eventDate.value?.monthValue ?: 0
        val year = eventDate.value?.year ?: 0
        val beginTime = eventBeginTime.value?.toNanoOfDay() ?: 0
        val endTime = eventEndTime.value?.toNanoOfDay() ?: 0
        val participantsLimit = participantsLimit.value ?: 0
        val participants = participants.value.orEmpty().map { user -> user.id }

        val creationDate = LocalDateTime.now().nano

        compositeDisposable.add(
            RxFirebaseAuth.getCurrentUser(Firebase.auth).toSingle()
                .flatMapCompletable { firebaseUser ->
                    EventsRepository.addEvent(
                        uid = firebaseUser.uid,
                        title = title,
                        description = description,
                        participants = participants,
                        participantsLimit = participantsLimit,
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
                    _loadingStatus.postValue(Status.Success)
                }, {
                    _loadingStatus.postValue(Status.Error)
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
        participantsLimit.value?.plus(value).let {
            _participantsLimit.value = if (it.isPositive()) it else 0
        }
    }

    fun loadParticipants(usersIds: List<String> = emptyList()) {
        if (usersIds != participantsIds) {
            _loadingStatus.postValue(Status.Loading)
            participantsIds = usersIds.toList()
            compositeDisposable.add(
                UsersRepository.getUsers(participantsIds).subscribe({ users ->
                    val participants = users.map { user -> user.mapToParticipantUi() }
                        .distinctBy { participant -> participant.id }

                    updateParticipantsLimit(participants.size)
                    _participants.postValue(participants)
                    _loadingStatus.postValue(Status.Success)
                }, {
                    _loadingStatus.postValue(Status.Error)
                })
            )
        }
    }

    fun removeParticipant(userId: String) {
        participants.value?.let { items ->
            val participantsCount = participantsIds.size
            participantsIds = participantsIds.filterNot { id -> id == userId }
            updateParticipantsLimit(participantsIds.size - participantsCount)
            _participants.postValue(
                items.toMutableList()
                    .filterNot { participant -> participant.id == userId }
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