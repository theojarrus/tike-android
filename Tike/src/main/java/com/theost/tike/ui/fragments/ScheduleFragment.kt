package com.theost.tike.ui.fragments

import android.animation.LayoutTransition
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import android.os.Bundle
import android.text.style.TextAppearanceSpan
import android.transition.TransitionManager
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.transition.TransitionManager.beginDelayedTransition
import androidx.transition.TransitionManager.endTransitions
import by.kirich1409.viewbindingdelegate.viewBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode.MONTHS
import com.prolificinteractive.materialcalendarview.CalendarMode.WEEKS
import com.theost.tike.R
import com.theost.tike.R.attr
import com.theost.tike.R.style
import com.theost.tike.databinding.FragmentScheduleBinding
import com.theost.tike.ui.adapters.basic.WeekDaysAdapter
import com.theost.tike.ui.adapters.callbacks.OnPageChangeCallback
import com.theost.tike.ui.adapters.pages.DayPageAdapter
import com.theost.tike.ui.decorators.DayDecorator
import com.theost.tike.ui.decorators.EventDecorator
import com.theost.tike.ui.decorators.TodayDecorator
import com.theost.tike.ui.extensions.fazy
import com.theost.tike.ui.utils.DateUtils.formatMonthYear
import com.theost.tike.ui.utils.ResUtils.getAttrColor
import com.theost.tike.ui.utils.ResUtils.getWeekDays
import com.theost.tike.ui.utils.ThemeUtils.isDarkTheme
import com.theost.tike.ui.viewmodels.CalendarViewModel
import com.theost.tike.ui.viewmodels.ScheduleViewModel
import com.theost.tike.ui.widgets.PageNumerator
import org.threeten.bp.LocalDate
import java.util.*
import java.util.Locale.getDefault

class ScheduleFragment : Fragment(R.layout.fragment_schedule) {

    private var todayDate: LocalDate = LocalDate.now()

    private val pagerNumerator: PageNumerator = PageNumerator { changeDay(it) }
    private lateinit var dayAdapter: DayPageAdapter

    private val eventDecorator: EventDecorator by fazy {
        EventDecorator(getAttrColor(requireContext(), attr.colorPrimary))
    }

    private val calendarViewModel: CalendarViewModel by activityViewModels()
    private val viewModel: ScheduleViewModel by viewModels()
    private val binding: FragmentScheduleBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupConfig()
        setupToolbar()
        setupCalendar()
        setupAdapter()
        setupPager()

        viewModel.currentPosition.observe(viewLifecycleOwner) { updatePagerDay(it) }
        viewModel.currentDay.observe(viewLifecycleOwner) { updateCalendarDay(it) }
        calendarViewModel.pendingDate.observe(viewLifecycleOwner) { pendingDate ->
            pendingDate?.let {
                changeDay(CalendarDay.from(it))
                calendarViewModel.setPendingDate(null)
            }
        }

        viewModel.events.observe(viewLifecycleOwner) { dates ->
            binding.calendarView.removeDecorator(eventDecorator)
            eventDecorator.dates = dates
            binding.calendarView.addDecorator(eventDecorator)
        }

        viewModel.init(dayAdapter.todayPosition)
    }

    private fun setupConfig() {
        Locale.setDefault(Locale("ru"))
        binding.root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun setupToolbar() {
        with(binding.toolbar) {
            when (isDarkTheme(requireContext())) {
                true -> menu.findItem(R.id.menuTodayDark).isVisible = true
                false -> menu.findItem(R.id.menuTodayLight).isVisible = true
            }
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menuTodayDark -> viewModel.setToday(dayAdapter.todayPosition)
                    R.id.menuTodayLight -> viewModel.setToday(dayAdapter.todayPosition)
                    R.id.menuCalendar -> switchCalendarMode()
                }
                true
            }
        }
    }

    private fun setupCalendar() {
        binding.weekDaysView.adapter = WeekDaysAdapter(getWeekDays(requireContext(), getDefault()))
        with(binding.calendarView) {
            rootView.findViewById<LinearLayout>(R.id.header).isGone = true
            addDecorators(
                DayDecorator(getDrawable(requireContext(), R.drawable.bg_date)),
                TodayDecorator(
                    TextAppearanceSpan(requireContext(), style.Theme_Tike_Date_Today),
                    todayDate
                )
            )
            setOnDateChangedListener { _, date, _ ->
                changeDay(date)
                updateToolbarDate()
            }
        }
    }

    private fun setupAdapter() {
        with(DayPageAdapter(childFragmentManager, lifecycle, todayDate)) {
            binding.daysPager.adapter = this
            dayAdapter = this
        }
    }

    private fun setupPager() {
        with(binding.daysPager) {
            registerOnPageChangeCallback(OnPageChangeCallback() { positionOffset ->
                pagerNumerator.calculatePosition(positionOffset)
            })
        }
    }

    private fun switchCalendarMode() {
        beginDelayedTransition(binding.calendarView)
        if (SDK_INT >= M) endTransitions(binding.toolbar)
        when (binding.calendarView.calendarMode ?: MONTHS) {
            WEEKS -> binding.calendarView.state().edit().setCalendarDisplayMode(MONTHS).commit()
            MONTHS -> binding.calendarView.state().edit().setCalendarDisplayMode(WEEKS).commit()
        }
    }

    private fun updateToolbarDate() {
        with(binding) {
            calendarView.selectedDate?.let { toolbar.title = formatMonthYear(it.month, it.year) }
        }
    }

    private fun updatePagerDay(position: Int) {
        if (pagerNumerator.position != position) {
            pagerNumerator.position = position
            binding.daysPager.setCurrentItem(position, false)
        }
    }

    private fun updateCalendarDay(day: CalendarDay) {
        binding.calendarView.currentDate = day
        binding.calendarView.selectedDate = day
        updateToolbarDate()
    }

    private fun changeDay(day: CalendarDay) {
        with(day.date.toEpochDay() - todayDate.toEpochDay() + dayAdapter.todayPosition) {
            when {
                this < 0 -> changeDay(0)
                this > dayAdapter.itemCount -> changeDay(dayAdapter.itemCount)
                else -> viewModel.updateCurrentDay(day, this.toInt())
            }
        }
    }

    private fun changeDay(position: Int) {
        viewModel.updateCurrentDay(
            CalendarDay.from(todayDate.plusDays((position - dayAdapter.todayPosition).toLong())),
            position
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.daysPager.adapter = null
    }
}
