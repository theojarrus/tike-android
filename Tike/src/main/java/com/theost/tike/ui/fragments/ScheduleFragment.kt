package com.theost.tike.ui.fragments

import android.animation.LayoutTransition
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.theost.tike.R
import com.theost.tike.databinding.FragmentScheduleBinding
import com.theost.tike.ui.adapters.core.DayAdapter
import com.theost.tike.ui.decorators.*
import com.theost.tike.ui.interfaces.CalendarHolder
import com.theost.tike.ui.viewmodels.ScheduleViewModel
import com.theost.tike.ui.widgets.PagerNumerator
import com.theost.tike.utils.DateUtils
import org.threeten.bp.LocalDate
import java.util.*

class ScheduleFragment : Fragment() {

    private var currentDate: LocalDate = LocalDate.now()

    private lateinit var dayAdapter: DayAdapter
    private lateinit var dayDecorator: SelectedEventDecorator
    private val pagerNumerator: PagerNumerator = PagerNumerator()

    private val viewModel: ScheduleViewModel by viewModels()

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(layoutInflater, container, false)

        viewModel.currentPosition.observe(viewLifecycleOwner) { changePagerDay(it) }
        viewModel.currentDay.observe(viewLifecycleOwner) { changeCalendarDay(it) }
        viewModel.currentDate.observe(viewLifecycleOwner) { currentDate = it }

        // Locale
        Locale.setDefault(Locale("ru"))

        // Transitions
        binding.root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        // Toolbar Items
        when (context?.resources?.configuration?.uiMode?.minus(1)) {
            Configuration.UI_MODE_NIGHT_NO ->
                binding.toolbar.findViewById<View>(R.id.menuTodayDark).visibility = View.GONE
            Configuration.UI_MODE_NIGHT_YES ->
                binding.toolbar.findViewById<View>(R.id.menuTodayLight).visibility = View.GONE
        }

        // Toolbar Listener
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuCalendar -> switchCalendarMode()
                R.id.menuTodayLight -> selectToday()
                R.id.menuTodayDark -> selectToday()
            }
            true
        }

        // Calendar
        val weekDaysArrayId = if (Locale.getDefault() == Locale("ru")) {
            R.array.week_days_ru
        } else {
            R.array.week_days_en
        }

        binding.calendarView.rootView.findViewById<LinearLayout>(R.id.header).visibility = View.GONE
        binding.weekDaysView.adapter = WeekDaysAdapter(resources.getStringArray(weekDaysArrayId))

        // Calendar Decorators
        context?.let { context ->
            dayDecorator = SelectedEventDecorator(context, CalendarDay.from(currentDate))
            binding.calendarView.addDecorators(
                SelectionDecorator(context),
                EventDecorator(context),
                TodayDecorator(context),
                dayDecorator
            )
        }

        // Calendar Listeners
        binding.calendarView.setOnDateChangedListener { _, date, _ ->
            if (!date.isBefore(CalendarDay.from(currentDate))) changeDay(date) else selectToday()
            updateToolbarDate()
        }

        // ViewPager2
        dayAdapter = DayAdapter(childFragmentManager, lifecycle, currentDate) {
            binding.daysPager.post { dayAdapter.loadNextDays() }
        }

        binding.daysPager.apply {
            (getChildAt(0) as? RecyclerView)?.overScrollMode = View.OVER_SCROLL_NEVER
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    if (pagerNumerator.updatePosition(positionOffset)) {
                        val day = CalendarDay.from(currentDate.plusDays(pagerNumerator.pagerPosition.toLong()))
                        val pagerPosition = pagerNumerator.pagerPosition
                        viewModel.updateCurrentDay(day, pagerPosition)
                    }
                }
            })
            adapter = dayAdapter
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val activeDate = (activity as CalendarHolder).getActiveDate()
        if (activeDate != null) changeDay(CalendarDay.from(activeDate))
    }

    private fun switchCalendarMode() {
        TransitionManager.beginDelayedTransition(binding.calendarView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            TransitionManager.endTransitions(binding.toolbar)
        when (binding.calendarView.calendarMode ?: CalendarMode.MONTHS) {
            CalendarMode.WEEKS -> binding.calendarView.state().edit()
                .setCalendarDisplayMode(CalendarMode.MONTHS).commit()
            CalendarMode.MONTHS -> binding.calendarView.state().edit()
                .setCalendarDisplayMode(CalendarMode.WEEKS).commit()
        }
    }

    private fun updateToolbarDate() {
        binding.calendarView.selectedDate?.let { date ->
            binding.toolbar.title = DateUtils.formatMonthYear(date.month, date.year)
        }
    }

    private fun changePagerDay(position: Int) {
        if (pagerNumerator.pagerPosition != position) {
            pagerNumerator.pagerPosition = position
            binding.daysPager.setCurrentItem(position, false)
        }
    }

    private fun changeCalendarDay(day: CalendarDay) {
        binding.calendarView.currentDate = day
        binding.calendarView.selectedDate = day

        binding.calendarView.removeDecorator(dayDecorator)
        dayDecorator.setDay(day)
        binding.calendarView.addDecorator(dayDecorator)

        updateToolbarDate()
    }

    private fun changeDay(day: CalendarDay) {
        viewModel.updateCurrentDay(day, (day.date.toEpochDay() - currentDate.toEpochDay()).toInt())
    }

    private fun selectToday() {
        viewModel.setToday()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}