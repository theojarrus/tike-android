package com.theost.tike.feature.creation.main.ui.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.theost.tike.feature.creation.adding.main.ui.CreationFragment
import com.theost.tike.feature.creation.joining.main.ui.JoiningFragment

class AddingPageAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CreationFragment.newInstance()
            1 -> JoiningFragment.newInstance()
            else -> Fragment()
        }
    }
}