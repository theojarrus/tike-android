package com.theost.tike.core.component.ui

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import com.theost.tike.common.util.DisplayUtils.hideKeyboard
import com.theost.tike.common.util.DisplayUtils.showError
import com.theost.tike.core.component.model.StateStatus
import com.theost.tike.core.component.model.StateStatus.*
import com.theost.tike.core.component.model.StateViews
import com.theost.tike.core.component.presentation.BaseState
import com.theost.tike.core.component.presentation.BaseStateViewModel

abstract class BaseStateActivity<State : BaseState, ViewModel : BaseStateViewModel<State>>(
    @LayoutRes contentLayoutId: Int
) : BaseRxActivity(contentLayoutId) {

    protected abstract val viewModel: ViewModel

    protected abstract val isHandlingState: Boolean
    protected abstract val isLoadingEndless: Boolean
    protected abstract val isRefreshingErrorOnly: Boolean

    protected abstract fun setupView()
    protected abstract fun render(state: State)

    protected abstract val stateViews: StateViews
    protected abstract val initialState: State
    protected abstract val initialAction: ViewModel.() -> Unit

    protected open val refreshAction: ViewModel.() -> Unit
        get() = initialAction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        let { activity ->
            viewModel.apply {
                state.observe(activity) { state ->
                    if (isHandlingState) handleStatus(state.status)
                    render(state)
                }
                init(initialState) { initialAction(this) }
                stateViews.swipeRefresh?.setOnRefreshListener { refreshAction(this) }
            }
        }
    }

    open fun handleStatus(status: StateStatus) {
        when (status) {
            Error -> {
                handleLoading(isLoading = false)
                handleRefreshing(isRefreshing = false)
                handleError(isError = true)
                controlRefreshing(isEnabled = true)
            }
            Initial -> {}
            Loading -> {
                handleLoading(isLoading = true)
                controlRefreshing(isEnabled = false)
            }
            Refreshing -> handleRefreshing(isRefreshing = true)
            Success -> {
                if (!isLoadingEndless) handleLoading(isLoading = false)
                handleError(isError = false)
                handleRefreshing(isRefreshing = false)
                controlRefreshing(isEnabled = !isRefreshingErrorOnly)
            }
        }
    }

    private fun handleError(isError: Boolean) {
        stateViews.errorView
            ?.let { it.isGone = !isError }
            ?: let { if (isError) stateViews.errorMessage?.let { showError(this, it) } }
    }

    private fun handleLoading(isLoading: Boolean) {
        with(stateViews) {
            rootView?.let { hideKeyboard(rootView) }
            actionView?.isInvisible = isLoading
            loadingView?.isGone = !isLoading
            disabledAdapter?.setEnabled(!isLoading)
            disabledViews.forEach { it.isEnabled = !isLoading }
        }
    }

    private fun handleRefreshing(isRefreshing: Boolean) {
        stateViews.swipeRefresh?.isRefreshing = isRefreshing
    }

    private fun controlRefreshing(isEnabled: Boolean) {
        stateViews.swipeRefresh?.isEnabled = isEnabled
    }
}
