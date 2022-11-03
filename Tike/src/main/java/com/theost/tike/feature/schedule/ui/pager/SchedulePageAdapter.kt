package com.theost.tike.feature.schedule.ui.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.theost.tike.feature.day.ui.DayFragment
import org.threeten.bp.LocalDate

class SchedulePageAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    var todayDay: LocalDate? = null
    var todayPosition: Int = DAYS_COUNT / 2

    override fun getItemCount(): Int = DAYS_COUNT

    override fun createFragment(position: Int): Fragment {
        val day = todayDay?.plusDays((position - todayPosition).toLong())
        return DayFragment.newInstance(day)
    }

    companion object {

        private const val DAYS_COUNT = 10000
    }
}
