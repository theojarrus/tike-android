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
import com.theost.tike.data.viewmodels.ScheduleViewModel
import com.theost.tike.databinding.FragmentScheduleBinding
import com.theost.tike.ui.adapters.core.DayAdapter
import com.theost.tike.ui.decorators.*
import com.theost.tike.ui.interfaces.CalendarHolder
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

        viewModel.currentPosition.observe(viewLifecycleOwner) { position ->
            pagerNumerator.pagerPosition = position
        }

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
                        viewModel.updateCurrentPosition(pagerNumerator.pagerPosition)
                        changeCalendarDay(CalendarDay.from(currentDate.plusDays(pagerNumerator.pagerPosition.toLong())))
                    }
                }
            })
            adapter = dayAdapter
        }

        // Startup
        selectToday()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val activeDate = (activity as CalendarHolder).getActiveDate()
        if (activeDate != null) {
            val activeDay = CalendarDay.from(activeDate)
            changeDay(activeDay)
            changeCalendarDay(activeDay)
        }
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

    private fun selectToday() {
        changePagerDay(0)
        changeCalendarDay(CalendarDay.from(currentDate))
    }

    private fun changeDay(day: CalendarDay) {
        binding.calendarView.removeDecorator(dayDecorator)
        changePagerDay(day.date.dayOfYear - CalendarDay.from(currentDate).date.dayOfYear)
        dayDecorator.setDay(binding.calendarView.selectedDate)
        binding.calendarView.addDecorator(dayDecorator)
    }

    private fun changeCalendarDay(day: CalendarDay) {
        binding.calendarView.currentDate = day
        binding.calendarView.selectedDate = day
        updateToolbarDate()
    }

    private fun changePagerDay(position: Int) {
        pagerNumerator.pagerPosition = position
        binding.daysPager.setCurrentItem(position, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.daysPager.adapter = null
        _binding = null
    }

    companion object {
        fun newInstance(): Fragment {
            return ScheduleFragment()
        }
    }

}