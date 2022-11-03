package com.theost.tike.feature.schedule.ui

import android.animation.LayoutTransition.CHANGING
import android.text.style.TextAppearanceSpan
import android.view.View
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.transition.TransitionManager.beginDelayedTransition
import by.kirich1409.viewbindingdelegate.viewBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarDay.today
import com.prolificinteractive.materialcalendarview.CalendarMode.MONTHS
import com.prolificinteractive.materialcalendarview.CalendarMode.WEEKS
import com.theost.tike.R
import com.theost.tike.common.extension.Locale.RU
import com.theost.tike.common.extension.registerOnPageChangeCallback
import com.theost.tike.common.extension.setSingleOnItemClickListener
import com.theost.tike.common.extension.updateDecorator
import com.theost.tike.common.pager.PageNumerator
import com.theost.tike.common.util.DateUtils.formatMonthYear
import com.theost.tike.common.util.DateUtils.subtractEpochDay
import com.theost.tike.common.util.ResUtils.getAttrColor
import com.theost.tike.common.util.ResUtils.getWeekDays
import com.theost.tike.core.model.StateStatus.Initial
import com.theost.tike.core.model.StateViews
import com.theost.tike.core.ui.BaseStateFragment
import com.theost.tike.databinding.FragmentScheduleBinding
import com.theost.tike.domain.model.core.Dates
import com.theost.tike.feature.day.ui.recycler.WeekDaysAdapter
import com.theost.tike.feature.day.ui.recycler.decorator.DayDecorator
import com.theost.tike.feature.day.ui.recycler.decorator.EventDecorator
import com.theost.tike.feature.day.ui.recycler.decorator.TodayDecorator
import com.theost.tike.feature.schedule.presentation.ScheduleState
import com.theost.tike.feature.schedule.presentation.ScheduleViewModel
import com.theost.tike.feature.schedule.ui.pager.SchedulePageAdapter
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.now
import java.util.*

class ScheduleFragment : BaseStateFragment<ScheduleState, ScheduleViewModel>(
    R.layout.fragment_schedule
) {

    private val binding: FragmentScheduleBinding by viewBinding()

    private lateinit var pageAdapter: SchedulePageAdapter
    private val pagerNumerator: PageNumerator = PageNumerator { changeDay(getDay(it)) }
    private val eventDecorator: EventDecorator by lazy {
        EventDecorator(getAttrColor(requireContext(), R.attr.colorPrimary))
    }

    override val viewModel: ScheduleViewModel by viewModels()

    override val isHandlingState: Boolean = false
    override val isRefreshingErrorOnly: Boolean = true

    private val todayDate: LocalDate?
        get() = viewModel.state.value?.today

    override fun setupView() = with(binding) {
        setupConfig()
        setupToolbar()
        setupCalendar()
        setupPager()
    }

    private fun setupConfig() {
        Locale.setDefault(RU)
        binding.root.layoutTransition.enableTransitionType(CHANGING)
    }

    private fun setupToolbar() = with(binding.toolbar) {
        setSingleOnItemClickListener { item ->
            when (item.itemId) {
                R.id.menuToday -> viewModel.changeDay(today(), pageAdapter.todayPosition)
                R.id.menuCalendar -> switchCalendarMode()
            }
        }
    }

    private fun setupCalendar() = with(binding) {
        weekDaysView.adapter = WeekDaysAdapter(getWeekDays(requireContext(), RU))
        calendarView.apply {
            rootView.findViewById<View>(R.id.header).isGone = true
            setOnDateChangedListener { _, day, _ -> changeDay(day) }
            addDecorator(DayDecorator(getDrawable(requireContext(), R.drawable.bg_date)))
        }
    }

    private fun setupPager() = with(binding.daysPager) {
        registerOnPageChangeCallback { pagerNumerator.calculatePosition(it) }
        pageAdapter = SchedulePageAdapter(childFragmentManager, lifecycle)
        adapter = pageAdapter
    }

    override fun render(state: ScheduleState) = with(binding) {
        renderToolbar(state)
        renderInitial(state)
        renderCalendar(state)
        renderPager(state)
        renderDates(state)
    }

    private fun renderToolbar(state: ScheduleState) = with(binding.toolbar) {
        title = formatMonthYear(state.day.month, state.day.year)
    }

    private fun renderInitial(state: ScheduleState) = with(binding.calendarView) {
        if (pageAdapter.todayDay != state.today) {
            pageAdapter.todayDay = state.today
            addDecorator(
                TodayDecorator(
                    TextAppearanceSpan(requireContext(), R.style.Theme_Tike_Date_Today),
                    state.today
                )
            )
        }
    }

    private fun renderCalendar(state: ScheduleState) = with(binding.calendarView) {
        if (selectedDate != state.day) {
            currentDate = state.day
            selectedDate = state.day
        }
    }

    private fun renderPager(state: ScheduleState) = with(binding.daysPager) {
        if (pagerNumerator.position != state.position) {
            pagerNumerator.position = state.position
            setCurrentItem(state.position, false)
        }
    }

    private fun renderDates(state: ScheduleState) = with(binding.calendarView) {
        updateDecorator(eventDecorator) { it.dates = state.dates }
    }

    private fun changeDay(day: CalendarDay) {
        val position = subtractEpochDay(day.date, todayDate).toInt() + pageAdapter.todayPosition
        val itemCount = pageAdapter.itemCount
        when {
            position < 0 -> viewModel.changeDay(getDay(0), position)
            position > itemCount -> viewModel.changeDay(getDay(itemCount), position)
            else -> viewModel.changeDay(day, position)
        }
    }

    private fun getDay(position: Int): CalendarDay {
        val day = todayDate?.plusDays((position - pageAdapter.todayPosition).toLong()) ?: now()
        return CalendarDay.from(day)
    }

    private fun switchCalendarMode() = with(binding.calendarView) {
        beginDelayedTransition(this)
        when (calendarMode) {
            WEEKS -> state().edit().setCalendarDisplayMode(MONTHS).commit()
            MONTHS -> state().edit().setCalendarDisplayMode(WEEKS).commit()
            else -> state().edit().setCalendarDisplayMode(WEEKS).commit()
        }
    }

    override val stateViews: StateViews
        get() = StateViews()

    override val initialState: ScheduleState
        get() = ScheduleState(
            status = Initial,
            dates = Dates(),
            today = now(),
            day = today(),
            position = pageAdapter.todayPosition
        )

    override val initialAction: ScheduleViewModel.() -> Unit = {
        observeDates()
    }
}
