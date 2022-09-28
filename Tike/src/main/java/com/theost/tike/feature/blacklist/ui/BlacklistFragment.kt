package com.theost.tike.feature.blacklist.ui

import android.view.MenuItem
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.extension.pressBack
import com.theost.tike.common.recycler.base.BaseAdapter
import com.theost.tike.core.component.model.StateStatus.Initial
import com.theost.tike.core.component.model.StateViews
import com.theost.tike.core.component.ui.BaseSearchStateFragment
import com.theost.tike.core.recycler.user.UserAdapterDelegate
import com.theost.tike.databinding.FragmentBlacklistBinding
import com.theost.tike.feature.blacklist.presentation.BlacklistState
import com.theost.tike.feature.blacklist.presentation.BlacklistViewModel
import com.theost.tike.feature.blacklist.ui.BlacklistFragmentDirections.Companion.actionBlacklistToProfile

class BlacklistFragment : BaseSearchStateFragment<BlacklistState, BlacklistViewModel>(
    R.layout.fragment_blacklist
) {

    private val binding: FragmentBlacklistBinding by viewBinding()
    private val adapter: BaseAdapter = BaseAdapter()

    override val searchMenuItem: MenuItem
        get() = binding.toolbar.menu.findItem(R.id.menuSearch)

    override val viewModel: BlacklistViewModel by viewModels()

    override val isHandlingState: Boolean = true
    override val isLoadingEndless: Boolean = false
    override val isRefreshingErrorOnly: Boolean = true

    override fun setupView() = with(binding) {
        toolbar.setNavigationOnClickListener { activity.pressBack() }
        recyclerView.adapter = adapter.apply {
            addDelegate(UserAdapterDelegate { uid ->
                findNavController().navigate(actionBlacklistToProfile(uid))
            })
        }
    }

    override fun render(state: BlacklistState) {
        binding.emptyView.isGone = !state.status.isLoaded() || state.items.isNotEmpty()
        adapter.submitList(state.items)
    }

    override val stateViews: StateViews
        get() = StateViews(
            swipeRefresh = binding.swipeRefresh,
            loadingView = binding.loadingView,
            errorView = binding.errorView
        )

    override val initialState: BlacklistState
        get() = BlacklistState(
            status = Initial,
            items = emptyList(),
            cache = emptyList(),
            query = null
        )

    override val initialAction: BlacklistViewModel.() -> Unit = {
        observeBlacklist()
    }
}
