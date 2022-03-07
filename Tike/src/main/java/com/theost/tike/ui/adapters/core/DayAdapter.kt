package com.theost.tike.ui.adapters.core

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.theost.tike.ui.fragments.DayFragment
import org.threeten.bp.LocalDate

class DayAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val beginDay: LocalDate,
    private val paginationCallback: () -> Unit
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private var daysCount: Int = PAGINATION_DAYS_COUNT

    override fun createFragment(position: Int): Fragment {
        return DayFragment.newInstance(beginDay.plusDays(position.toLong()))
    }

    override fun getItemCount(): Int = daysCount

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        if (daysCount - position <= PAGINATION_BEFORE_LOAD) paginationCallback()
    }

    fun loadNextDays() {
        val daysBefore = daysCount
        daysCount = daysBefore + PAGINATION_DAYS_COUNT
        notifyItemRangeInserted(daysBefore, daysCount)
    }

    companion object {
        private const val PAGINATION_DAYS_COUNT = 20
        private const val PAGINATION_BEFORE_LOAD = 5
    }

}