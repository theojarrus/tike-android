package com.theost.tike.feature.schedule.ui.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.theost.tike.feature.schedule.ui.DayFragment
import org.threeten.bp.LocalDate

class DayPageAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val todayDay: LocalDate
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    var todayPosition: Int = DAYS_COUNT / 2

    override fun getItemCount(): Int = DAYS_COUNT

    override fun createFragment(position: Int): Fragment {
        return DayFragment.newInstance(todayDay.plusDays((position - todayPosition).toLong()))
    }

    companion object {

        private const val DAYS_COUNT = 10000
    }
}