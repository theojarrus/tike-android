package com.theost.tike.ui.adapters.core

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.theost.tike.ui.fragments.CreationFragment
import com.theost.tike.ui.fragments.DayFragment
import com.theost.tike.ui.fragments.JoiningFragment
import org.threeten.bp.LocalDate

class AddingAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            ADDING_CREATION_POSITION -> CreationFragment.newInstance()
            ADDING_JOINING_POSITION -> JoiningFragment.newInstance()
            else -> Fragment()
        }
    }

    override fun getItemCount(): Int = ADDING_MODES_COUNT

    companion object {
        const val ADDING_CREATION_POSITION = 0
        const val ADDING_JOINING_POSITION = 1
        const val ADDING_MODES_COUNT = 2;
    }

}