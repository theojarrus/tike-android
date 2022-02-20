package com.theost.tike.ui.fragments

import android.animation.LayoutTransition
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.theost.tike.R
import com.theost.tike.databinding.FragmentScheduleBinding
import com.theost.tike.utils.DateUtils
import com.theost.tike.widgets.*
import java.util.*


class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private lateinit var dayDecorator: DayDecorator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(layoutInflater)

        Locale.setDefault(Locale("ru"))
        setupCalendar()
        setupAnimations()
        setupToolbar()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupAnimations() {
        binding.root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun updateToolbar(date: CalendarDay = CalendarDay.today()) {
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

    private fun setupToolbar() {
        setupToolbarItems()
        updateToolbar()
    }

    private fun setupCalendar() {
        setupCalendarViews()
        setupCalendarDecorators()
        setupCalendarListeners()
        selectToday()
    }

    private fun setupCalendarViews() {
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
            dayDecorator = DayDecorator(context, CalendarDay.today())
            binding.calendarView.addDecorators(
                SelectionDecorator(context),
                EventDecorator(context),
                TodayDecorator(context),
                dayDecorator
            )
        }
    }

    private fun setupCalendarListeners() {
        binding.calendarView.setOnMonthChangedListener { _, date -> updateToolbar(date) }
        binding.calendarView.setOnDateChangedListener { _, date, selected ->
            if (selected) switchDayFragment(date)
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

    private fun selectToday() {
        binding.calendarView.currentDate = CalendarDay.today()
        binding.calendarView.selectedDate = CalendarDay.today()
    }

    private fun switchDayFragment(date: CalendarDay) {
        // todo
    }

    companion object {
        fun newInstance(context: Context): Fragment {
            return ScheduleFragment()
        }
    }

}