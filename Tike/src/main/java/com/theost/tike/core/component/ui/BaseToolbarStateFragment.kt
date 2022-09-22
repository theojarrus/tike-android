package com.theost.tike.core.component.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import com.theost.tike.R
import com.theost.tike.common.extension.pressBack
import com.theost.tike.core.component.presentation.BaseState
import com.theost.tike.core.component.presentation.BaseStateViewModel

abstract class BaseToolbarStateFragment<State : BaseState, ViewModel : BaseStateViewModel<State>>(
    @LayoutRes contentLayoutId: Int
) : BaseStateFragment<State, ViewModel>(contentLayoutId) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
    }

    private fun setupToolbar() {
        with(stateViews) {
            toolbar?.setNavigationIcon(R.drawable.ic_back)
            toolbar?.setNavigationOnClickListener { activity.pressBack() }
        }
    }
}
