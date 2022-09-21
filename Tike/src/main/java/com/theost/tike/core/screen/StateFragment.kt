package com.theost.tike.core.screen

import android.view.View
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.annotation.LayoutRes
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.theost.tike.common.recycler.base.BaseAdapter
import com.theost.tike.common.util.DisplayUtils.hideKeyboard
import com.theost.tike.domain.model.multi.Status
import com.theost.tike.domain.model.multi.Status.*

abstract class StateFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    protected data class StateViews(
        val toolbar: MaterialToolbar? = null,
        val rootView: View? = null,
        val actionView: View? = null,
        val loadingView: View? = null,
        val errorView: View? = null,
        val errorMessage: String? = null,
        val disabledAdapter: BaseAdapter? = null,
        val disabledViews: List<View> = emptyList()
    )

    protected abstract fun bindState(): StateViews

    fun handleStatus(status: Status) {
        when (status) {
            Loading -> {
                hideError()
                showLoading()
            }
            Success -> {
                hideError()
                hideLoading()
            }
            Error -> {
                hideLoading()
                showError()
            }
        }
    }

    fun handleEndlessStatus(status: Status) {
        when (status) {
            Loading -> {
                hideError()
                showLoading()
            }
            Success -> {
                hideError()
            }
            Error -> {
                hideLoading()
                showError()
            }
        }
    }

    private fun showLoading() {
        with(bindState()) {
            rootView?.let { hideKeyboard(rootView) }
            actionView?.isInvisible = true
            loadingView?.isGone = false
            disabledAdapter?.setEnabled(false)
            disabledViews.forEach {
                it.isEnabled = false
                it.clearFocus()
            }
        }
    }

    private fun hideLoading() {
        with(bindState()) {
            actionView?.isInvisible = false
            loadingView?.isGone = true
            disabledAdapter?.setEnabled(true)
            disabledViews.forEach { it.isEnabled = true }
        }
    }

    private fun showError() {
        with(bindState()) {
            errorView?.let { it.isGone = false } ?: showErrorToast()
        }
    }

    private fun hideError() {
        with(bindState()) {
            errorView?.isGone = true
        }
    }

    fun showErrorToast() {
        bindState().errorMessage?.let { makeText(context, it, Toast.LENGTH_SHORT).show() }
    }
}
