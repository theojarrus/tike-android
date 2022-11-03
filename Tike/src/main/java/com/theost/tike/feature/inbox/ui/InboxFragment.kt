package com.theost.tike.feature.inbox.ui

import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.extension.navigate
import com.theost.tike.common.recycler.base.BaseAdapter
import com.theost.tike.common.recycler.element.event.EventAdapterDelegate
import com.theost.tike.common.recycler.element.friend.FriendAdapterDelegate
import com.theost.tike.common.recycler.element.title.TitleAdapterDelegate
import com.theost.tike.core.model.StateStatus.Initial
import com.theost.tike.core.model.StateViews
import com.theost.tike.core.ui.BaseStateFragment
import com.theost.tike.databinding.FragmentInboxBinding
import com.theost.tike.domain.model.multi.EventAction.Info
import com.theost.tike.domain.model.multi.FriendAction
import com.theost.tike.feature.inbox.presentation.InboxState
import com.theost.tike.feature.inbox.presentation.InboxViewModel
import com.theost.tike.feature.inbox.ui.InboxFragmentDirections.Companion.actionInboxToInfo
import com.theost.tike.feature.inbox.ui.InboxFragmentDirections.Companion.actionInboxToProfile

class InboxFragment : BaseStateFragment<InboxState, InboxViewModel>(R.layout.fragment_inbox) {

    private val binding: FragmentInboxBinding by viewBinding()
    private val adapter: BaseAdapter = BaseAdapter()

    override val viewModel: InboxViewModel by viewModels()

    override val isHandlingState: Boolean = true
    override val isRefreshingErrorOnly: Boolean = true

    override fun setupView() = with(binding) {
        recycler.adapter = adapter.apply {
            addDelegate(TitleAdapterDelegate())
            addDelegate(FriendAdapterDelegate { friendAction ->
                when (friendAction) {
                    is FriendAction.Info -> navigate(actionInboxToProfile(friendAction.uid))
                    else -> viewModel.dispatchFriendAction(friendAction)
                }
            })
            addDelegate(EventAdapterDelegate { eventAction ->
                when (eventAction) {
                    is Info -> navigate(actionInboxToInfo(eventAction.id, eventAction.creator))
                    else -> viewModel.dispatchEventAction(eventAction)
                }
            })
        }
    }

    override fun render(state: InboxState) = with(binding) {
        emptyView.isGone = !state.status.isLoaded() || state.items.isNotEmpty()
        adapter.submitList(state.items)
    }

    override val stateViews: StateViews
        get() = StateViews(
            swipeRefresh = binding.swipeRefresh,
            loadingView = binding.loadingView,
            errorView = binding.errorView
        )

    override val initialState: InboxState
        get() = InboxState(
            status = Initial,
            items = emptyList()
        )

    override val initialAction: InboxViewModel.() -> Unit = {
        observeInbox()
    }
}
