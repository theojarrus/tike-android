package com.theost.tike.feature.day.presentation

import com.theost.tike.common.extension.subscribe
import com.theost.tike.common.recycler.element.event.EventUiMapper
import com.theost.tike.common.recycler.element.user.UserUiMapper
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.core.model.StateStatus.Error
import com.theost.tike.core.model.StateStatus.Success
import com.theost.tike.core.presentation.BaseStateViewModel
import com.theost.tike.domain.model.multi.EventAction
import com.theost.tike.domain.repository.AuthRepository
import com.theost.tike.domain.repository.EventsRepository
import com.theost.tike.domain.repository.UsersRepository
import com.theost.tike.feature.actions.UpdateEventInteractor
import com.theost.tike.feature.day.business.ObserveDayEventsInteractor
import org.threeten.bp.LocalDate

class DayViewModel : BaseStateViewModel<DayState>() {

    private val eventMapper = EventUiMapper()
    private val userMapper = UserUiMapper()

    private val observeDayEventsInteractor = ObserveDayEventsInteractor(
        AuthRepository,
        EventsRepository,
        UsersRepository,
        eventMapper,
        userMapper
    )

    private val updateEventInteractor = UpdateEventInteractor(
        AuthRepository,
        EventsRepository
    )

    fun observeEvents(date: LocalDate?) {
        update { copy(status = status.getLoadingStatus()) }
        date?.let { day ->
            disposableSwitch {
                observeDayEventsInteractor(day)
                    .subscribe({ items ->
                        update { copy(status = Success, items = items) }
                    }, { error ->
                        update { copy(status = Error, items = emptyList()) }
                        log(this, error)
                    })
            }
        } ?: run {
            update { copy(status = Error, items = emptyList()) }
        }
    }

    fun dispatchEventAction(eventAction: EventAction) {
        update { copy(items = items.filterNot { it.id() == eventAction.item }) }
        disposable {
            updateEventInteractor(eventAction)
                .subscribe { error ->
                    update { copy(status = Error, items = emptyList()) }
                    log(this, error)
                }
        }
    }
}
