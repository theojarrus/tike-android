package com.theost.tike.core.screen

import androidx.annotation.LayoutRes
import com.theost.tike.R
import com.theost.tike.common.extension.pressBack

abstract class ToolbarStateFragment(
    @LayoutRes contentLayoutId: Int
) : StateFragment(contentLayoutId) {

    fun setupToolbar(isBackEnabled: Boolean) {
        with(bindState()) {
            if (isBackEnabled) {
                toolbar?.setNavigationIcon(R.drawable.ic_back)
                toolbar?.setNavigationOnClickListener { activity.pressBack() }
            } else {
                toolbar?.navigationIcon = null
                toolbar?.setNavigationOnClickListener(null)
            }
        }
    }
}
