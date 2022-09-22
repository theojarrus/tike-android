package com.theost.tike.core.component.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import com.theost.tike.common.util.DisplayUtils
import com.theost.tike.core.component.presentation.BaseState
import com.theost.tike.core.component.model.StateStatus
import com.theost.tike.core.component.model.StateStatus.*
import com.theost.tike.core.component.model.StateViews
import com.theost.tike.core.component.presentation.BaseStateViewModel

abstract class BaseStateFragment<State : BaseState, ViewModel : BaseStateViewModel<State>>(
    @LayoutRes contentLayoutId: Int
) : Fragment(contentLayoutId) {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        viewModel.apply {
            state.observe(viewLifecycleOwner) { state ->
                if (isHandlingState) handleStatus(state.status)
                render(state)
            }
            init(initialState) { initialAction(this) }
            stateViews.swipeRefresh?.setOnRefreshListener { refreshAction(this) }
        }
    }

    fun handleStatus(status: StateStatus) {
        when (status) {
            Error -> {
                hideLoading()
                hideRefreshing()
                showError()
                enableRefreshing()
            }
            Initial -> {}
            Loading -> {
                hideError()
                showLoading()
                disableRefreshing()
            }
            Refreshing -> {
                showRefreshing()
            }
            Success -> {
                hideError()
                hideRefreshing()
                if (!isLoadingEndless) hideLoading()
                if (!isRefreshingErrorOnly) enableRefreshing()
            }
        }
    }

    private fun showLoading() {
        with(stateViews) {
            rootView?.let { DisplayUtils.hideKeyboard(rootView) }
            actionView?.isInvisible = true
            loadingView?.isGone = false
            loadingView?.isGone = false
            disabledAdapter?.setEnabled(false)
            disabledViews.forEach { it.isEnabled = false }
        }
    }

    private fun hideLoading() {
        with(stateViews) {
            actionView?.isInvisible = false
            loadingView?.isGone = true
            disabledAdapter?.setEnabled(true)
            disabledViews.forEach { it.isEnabled = true }
        }
    }

    private fun showRefreshing() {
        stateViews.swipeRefresh?.isRefreshing = true
    }

    private fun hideRefreshing() {
        stateViews.swipeRefresh?.isRefreshing = false
    }

    private fun enableRefreshing() {
        stateViews.swipeRefresh?.isEnabled = true
    }

    private fun disableRefreshing() {
        stateViews.swipeRefresh?.isEnabled = false
    }

    private fun showError() {
        with(stateViews) {
            errorView?.let { it.isGone = false } ?: showErrorToast()
        }
    }

    private fun hideError() {
        with(stateViews) {
            errorView?.isGone = true
        }
    }

    private fun showErrorToast() {
        stateViews.errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }
}
