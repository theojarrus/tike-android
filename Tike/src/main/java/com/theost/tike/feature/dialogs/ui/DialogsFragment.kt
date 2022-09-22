package com.theost.tike.feature.dialogs.ui

import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.extension.fazy
import com.theost.tike.common.recycler.base.BaseAdapter
import com.theost.tike.common.util.DisplayUtils.showError
import com.theost.tike.core.component.model.StateStatus.Initial
import com.theost.tike.core.component.model.StateViews
import com.theost.tike.core.component.ui.BaseStateFragment
import com.theost.tike.core.recycler.user.UserAdapterDelegate
import com.theost.tike.databinding.FragmentDialogsBinding
import com.theost.tike.feature.dialogs.presentation.DialogsState
import com.theost.tike.feature.dialogs.presentation.DialogsViewModel

class DialogsFragment : BaseStateFragment<DialogsState, DialogsViewModel>(
    R.layout.fragment_dialogs
) {

    private val adapter: BaseAdapter = BaseAdapter()
    private val binding: FragmentDialogsBinding by viewBinding()

    override val viewModel: DialogsViewModel by viewModels()

    override val isHandlingState: Boolean = true
    override val isLoadingEndless: Boolean = false
    override val isRefreshingErrorOnly: Boolean = false

    override fun setupView(): Unit = with(binding) {
        recyclerView.adapter = adapter.apply {
            addDelegate(UserAdapterDelegate { showError(context, R.string.feature_not_ready) })
        }
    }

    override fun render(state: DialogsState) {
        binding.emptyView.isGone = !state.status.isLoaded() || state.items.isNotEmpty()
        adapter.submitList(state.items)
    }

    override val stateViews: StateViews by fazy {
        StateViews(
            toolbar = binding.toolbar,
            loadingView = binding.loadingBar,
            swipeRefresh = binding.swipeRefresh,
            errorView = binding.errorView,
            disabledAdapter = adapter
        )
    }

    override val initialState: DialogsState = DialogsState(
        status = Initial,
        items = emptyList()
    )

    override val initialAction: DialogsViewModel.() -> Unit = {
        fetchDialogs()
    }
}
