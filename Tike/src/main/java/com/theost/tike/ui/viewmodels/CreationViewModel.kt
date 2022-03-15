package com.theost.tike.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.tike.data.models.state.RepeatMode
import com.theost.tike.data.models.ui.ListParticipant
import com.theost.tike.data.repositories.EventsRepository
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.ui.mapToListParticipant
import com.theost.tike.data.repositories.UsersRepository
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

class CreationViewModel : ViewModel() {

    private val _sendingStatus = MutableLiveData<Status>()
    val sendingStatus: LiveData<Status> = _sendingStatus

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    private val _eventDate = MutableLiveData<LocalDate>()
    val eventDate: LiveData<LocalDate> = _eventDate

    private val _eventBeginTime = MutableLiveData<LocalTime>()
    val eventBeginTime: LiveData<LocalTime> = _eventBeginTime

    private val _eventEndTime = MutableLiveData<LocalTime>()
    val eventEndTime: LiveData<LocalTime> = _eventEndTime

    private val _participants = MutableLiveData<List<ListParticipant>>()
    val participants: LiveData<List<ListParticipant>> = _participants

    private var participantsIds = emptyList<String>()
    private val compositeDisposable = CompositeDisposable()

    init {
        val eventDateTime = LocalDateTime.now().plusHours(DATE_EVENT_DEFAULT_AFTER)
        val eventDate = eventDateTime.toLocalDate()
        val eventBeginTime = LocalTime.of(eventDateTime.hour, 0)
        val eventEndTime = eventBeginTime.plusHours(DATE_EVENT_DEFAULT_LENGTH)

        _eventDate.postValue(eventDate)
        _eventBeginTime.postValue(eventBeginTime)
        _eventEndTime.postValue(eventEndTime)
        _participants.postValue(emptyList())
    }

    fun sendEventData(title: String, description: String, repeatMode: RepeatMode) {
        if (title.isNotEmpty() && description.isNotEmpty()) {
            _sendingStatus.postValue(Status.Loading)

            val weekDay = eventDate.value?.dayOfWeek?.value ?: 0
            val monthDay = eventDate.value?.dayOfMonth ?: 0
            val month = eventDate.value?.monthValue ?: 0
            val year = eventDate.value?.year ?: 0
            val beginTime = eventBeginTime.value?.toNanoOfDay() ?: 0
            val endTime = eventEndTime.value?.toNanoOfDay() ?: 0
            val participants = participants.value.orEmpty().map { user -> user.id }

            val creationDate = LocalDateTime.now().nano

            compositeDisposable.add(
                EventsRepository.addEvent(
                    title = title,
                    description = description,
                    participants = participants,
                    created = creationDate,
                    modified = creationDate,
                    weekDay = weekDay,
                    monthDay = monthDay,
                    month = month,
                    year = year,
                    beginTime = beginTime,
                    endTime = endTime,
                    repeatMode = repeatMode.uiName
                ).subscribe({
                    _sendingStatus.postValue(Status.Success)
                }, { error ->
                    _sendingStatus.postValue(Status.Error(error))
                })
            )
        } else {
            _sendingStatus.postValue(Status.Error(Throwable("Event description is empty!")))
        }
    }

    fun updateEventDate(year: Int, month: Int, day: Int) {
        val date = LocalDate.of(year, month, day)
        if (!date.isBefore(LocalDate.now())) _eventDate.postValue(date)
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

    fun loadParticipants(usersIds: List<String> = emptyList()) {
        if (usersIds != participantsIds) {
            _loadingStatus.postValue(Status.Loading)
            if (usersIds.isNotEmpty()) participantsIds = usersIds.toList()
            compositeDisposable.add(UsersRepository.getUsers(participantsIds).subscribe({ users ->
                val participants = users.map { user -> user.mapToListParticipant() }
                    .distinctBy { participant -> participant.id }
                _participants.postValue(participants)
                _loadingStatus.postValue(Status.Success)
            }, { error ->
                _loadingStatus.postValue(Status.Error(error))
            }))
        }
    }

    fun removeParticipant(userId: String) {
        participants.value?.let { items ->
            participantsIds = participantsIds.filterNot { id -> id == userId }
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