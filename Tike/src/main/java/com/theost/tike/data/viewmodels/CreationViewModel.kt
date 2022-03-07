package com.theost.tike.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.tike.data.models.state.RepeatMode
import com.theost.tike.data.models.ui.ListParticipant
import com.theost.tike.data.repositories.EventsRepository
import com.theost.tike.data.state.Status
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

class CreationViewModel : ViewModel() {

    private val _sendingStatus = MutableLiveData<Status>()
    val sendingStatus: LiveData<Status> = _sendingStatus

    private val _eventDate = MutableLiveData<LocalDate>()
    val eventDate: LiveData<LocalDate> = _eventDate

    private val _eventBeginTime = MutableLiveData<LocalTime>()
    val eventBeginTime: LiveData<LocalTime> = _eventBeginTime

    private val _eventEndTime = MutableLiveData<LocalTime>()
    val eventEndTime: LiveData<LocalTime> = _eventEndTime

    private val _participants = MutableLiveData<List<ListParticipant>>()
    val participants: LiveData<List<ListParticipant>> = _participants

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
            _sendingStatus.postValue(Status.LOADING)

            val participants = participants.value?.map { it.id } ?: emptyList()
            val date = eventDate.value?.toEpochDay() ?: -1
            val beginTime = eventBeginTime.value?.toNanoOfDay() ?: -1
            val endTime = eventEndTime.value?.toNanoOfDay() ?: -1

            compositeDisposable.add(
                EventsRepository.addEvents(
                    title,
                    description,
                    participants,
                    date,
                    beginTime,
                    endTime,
                    repeatMode.uiName
                ).subscribe({
                    _sendingStatus.postValue(Status.SUCCESS)
                }, {
                    _sendingStatus.postValue(Status.ERROR)
                })
            )
        } else {
            _sendingStatus.postValue(Status.ERROR)
        }
    }

    fun removeParticipant(participantId: Int) {
        participants.value?.let { participants ->
            _participants.postValue(
                participants.toMutableList()
                    .filterNot { participant -> participant.id == participantId }
                    .toList()
            )
        }
    }

    fun updateParticipants(addedParticipants: List<ListParticipant>) {
        if (participants.value != null) {
            participants.value?.let { participants ->
                _participants.postValue(
                    participants.toMutableList()
                        .apply { addAll(addedParticipants) }
                        .toList()
                )
            }
        } else {
            _participants.postValue(addedParticipants)
        }
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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    companion object {
        private const val DATE_EVENT_DEFAULT_AFTER = 1L
        private const val DATE_EVENT_DEFAULT_LENGTH = 1L
    }

}