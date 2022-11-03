package com.theost.tike.feature.creation.ui.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.theost.tike.feature.adding.ui.CreationFragment
import com.theost.tike.feature.joining.ui.JoiningFragment

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
