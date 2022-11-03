package com.theost.tike.feature.dialogs.ui

import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.recycler.base.BaseAdapter
import com.theost.tike.common.recycler.element.user.UserAdapterDelegate
import com.theost.tike.common.util.DisplayUtils.showError
import com.theost.tike.core.model.StateStatus.Initial
import com.theost.tike.core.model.StateViews
import com.theost.tike.core.ui.BaseStateFragment
import com.theost.tike.databinding.FragmentDialogsBinding
import com.theost.tike.feature.dialogs.presentation.DialogsState
import com.theost.tike.feature.dialogs.presentation.DialogsViewModel

class DialogsFragment : BaseStateFragment<DialogsState, DialogsViewModel>(
    R.layout.fragment_dialogs
) {

    private val binding: FragmentDialogsBinding by viewBinding()
    private val adapter: BaseAdapter = BaseAdapter()

    override val viewModel: DialogsViewModel by viewModels()

    override val isHandlingState: Boolean = true
    override val isRefreshingErrorOnly: Boolean = false

    override fun setupView() = with(binding) {
        recyclerView.adapter = adapter.apply {
            addDelegate(UserAdapterDelegate { showError(context, R.string.feature_not_ready) })
        }
    }

    override fun render(state: DialogsState) = with(binding) {
        emptyView.isGone = !state.status.isLoaded() || state.items.isNotEmpty()
        adapter.submitList(state.items)
    }

    override val stateViews: StateViews
        get() = StateViews(
            swipeRefresh = binding.swipeRefresh,
            loadingView = binding.loadingView,
            errorView = binding.errorView
        )

    override val initialState: DialogsState
        get() = DialogsState(
            status = Initial,
            items = emptyList()
        )

    override val initialAction: DialogsViewModel.() -> Unit = {
        fetchDialogs()
    }
}
