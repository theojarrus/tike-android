package com.theost.tike.feature.friends.ui

import android.view.MenuItem
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.extension.pressBack
import com.theost.tike.common.recycler.base.BaseAdapter
import com.theost.tike.common.recycler.element.user.UserAdapterDelegate
import com.theost.tike.core.model.StateStatus.Initial
import com.theost.tike.core.model.StateViews
import com.theost.tike.core.ui.BaseSearchStateFragment
import com.theost.tike.databinding.FragmentFriendsBinding
import com.theost.tike.feature.friends.presentation.FriendsState
import com.theost.tike.feature.friends.presentation.FriendsViewModel
import com.theost.tike.feature.friends.ui.FriendsFragmentDirections.Companion.actionFriendsToPeople
import com.theost.tike.feature.friends.ui.FriendsFragmentDirections.Companion.actionFriendsToProfile

class FriendsFragment : BaseSearchStateFragment<FriendsState, FriendsViewModel>(
    R.layout.fragment_friends
) {

    private val binding: FragmentFriendsBinding by viewBinding()
    private val adapter: BaseAdapter = BaseAdapter()

    override val searchMenuItem: MenuItem
        get() = binding.toolbar.menu.findItem(R.id.menuSearch)

    override val viewModel: FriendsViewModel by viewModels()

    override val isHandlingState: Boolean = true
    override val isLoadingEndless: Boolean = false
    override val isRefreshingErrorOnly: Boolean = true

    override fun setupView() = with(binding) {
        toolbar.setNavigationOnClickListener { activity.pressBack() }
        add.setOnClickListener { findNavController().navigate(actionFriendsToPeople()) }
        recyclerView.adapter = adapter.apply {
            addDelegate(UserAdapterDelegate { uid ->
                findNavController().navigate(actionFriendsToProfile(uid))
            })
        }
    }

    override fun render(state: FriendsState) {
        binding.emptyView.isGone = !state.status.isLoaded() || state.items.isNotEmpty()
        adapter.submitList(state.items)
    }

    override val stateViews: StateViews
        get() = StateViews(
            swipeRefresh = binding.swipeRefresh,
            loadingView = binding.loadingView,
            errorView = binding.errorView
        )

    override val initialState: FriendsState
        get() = FriendsState(
            status = Initial,
            items = emptyList(),
            cache = emptyList(),
            query = null
        )

    override val initialAction: FriendsViewModel.() -> Unit = {
        observeFriends()
    }
}
