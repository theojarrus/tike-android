package com.theost.tike.ui.widgets

import androidx.annotation.LayoutRes
import com.theost.tike.R

abstract class ToolbarStateFragment(
    @LayoutRes contentLayoutId: Int
) : StateFragment(contentLayoutId) {

    fun setupToolbar(isBackEnabled: Boolean) {
        with(bindState()) {
            if (isBackEnabled) {
                toolbar?.setNavigationIcon(R.drawable.ic_back)
                toolbar?.setNavigationOnClickListener { activity?.onBackPressed() }
            } else {
                toolbar?.navigationIcon = null
                toolbar?.setNavigationOnClickListener(null)
            }
        }
    }
}