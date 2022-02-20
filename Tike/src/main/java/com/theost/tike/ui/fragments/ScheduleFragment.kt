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
import androidx.viewpager2.widget.ViewPager2
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.theost.tike.R
import com.theost.tike.databinding.FragmentScheduleBinding
import com.theost.tike.ui.adapters.DayAdapter
import com.theost.tike.ui.decorators.*
import com.theost.tike.ui.widgets.PagerNumerator
import com.theost.tike.utils.DateUtils
import java.util.*


class ScheduleFragment : Fragment() {

    private lateinit var dayDecorator: SelectedEventDecorator
    private val pagerNumerator: PagerNumerator = PagerNumerator()

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(layoutInflater)

        binding.root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        Locale.setDefault(Locale("ru"))

        setupToolbar()
        setupCalendar()
        setupPager()

        return binding.root
    }

    private fun setupToolbar() {
        setupToolbarItems()
        updateToolbarDate()
    }

    private fun updateToolbarDate(date: CalendarDay = CalendarDay.today()) {
        binding.toolbar.title = DateUtils.formatMonthYear(date.month, date.year)
    }

    private fun setupToolbarItems() {
        when (context?.resources?.configuration?.uiMode?.minus(1)) {
            Configuration.UI_MODE_NIGHT_NO ->
                binding.toolbar.findViewById<View>(R.id.menuTodayDark).visibility = View.GONE
            Configuration.UI_MODE_NIGHT_YES ->
                binding.toolbar.findViewById<View>(R.id.menuTodayLight).visibility = View.GONE
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuCalendar -> switchCalendarMode()
                R.id.menuTodayLight -> selectToday()
                R.id.menuTodayDark -> selectToday()
            }
            true
        }
    }

    private fun setupPager() {
        binding.daysPager.adapter = DayAdapter(childFragmentManager, lifecycle)
        binding.daysPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                pagerNumerator.updatePosition(positionOffset)
                switchCalendarSelection(pagerNumerator.pagerPosition)
            }
        })
    }

    private fun setupCalendar() {
        setupCalendarViews()
        setupCalendarDecorators()
        setupCalendarListeners()
        selectToday()
    }

    private fun setupCalendarViews() {
        binding.calendarView.setHeaderTextAppearance(0)
        binding.calendarView.rootView.findViewById<LinearLayout>(R.id.header).visibility = View.GONE
        binding.weekDaysView.adapter = WeekDaysAdapter(
            resources.getStringArray(
                if (Locale.getDefault() == Locale("ru"))
                    R.array.week_days_ru
                else
                    R.array.week_days_en
            )
        )
    }

    private fun setupCalendarDecorators() {
        context?.let { context ->
            dayDecorator = SelectedEventDecorator(context, CalendarDay.today())
            binding.calendarView.addDecorators(
                SelectionDecorator(context),
                EventDecorator(context),
                TodayDecorator(context),
                dayDecorator
            )
        }
    }

    private fun setupCalendarListeners() {
        binding.calendarView.setOnMonthChangedListener { _, date -> updateToolbarDate(date) }
        binding.calendarView.setOnDateChangedListener { _, date, _ ->
            switchDayFragment(date)
            updateDayDecorator()
        }
    }

    private fun updateDayDecorator() {
        binding.calendarView.removeDecorator(dayDecorator)
        dayDecorator.setDay(binding.calendarView.selectedDate)
        binding.calendarView.addDecorator(dayDecorator)
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

    private fun selectDay(position: Int) {
        pagerNumerator.pagerPosition = position
        binding.daysPager.setCurrentItem(position, false)
    }

    private fun selectToday() = selectDay(0)

    private fun switchCalendarSelection(position: Int) {
        val selectedDay = CalendarDay.from(CalendarDay.today().date.plusDays(position.toLong()))
        binding.calendarView.currentDate = selectedDay
        binding.calendarView.selectedDate = selectedDay
    }

    private fun switchDayFragment(selectedDay: CalendarDay) {
        selectDay(selectedDay.date.dayOfYear - CalendarDay.today().date.dayOfYear)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance(): Fragment {
            return ScheduleFragment()
        }
    }

}