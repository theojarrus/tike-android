package com.theost.tike.ui.adapters.pages

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.theost.tike.ui.fragments.CreationFragment
import com.theost.tike.ui.fragments.JoiningFragment

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