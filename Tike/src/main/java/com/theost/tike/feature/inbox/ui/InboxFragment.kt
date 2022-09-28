package com.theost.tike.feature.inbox.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.recycler.base.BaseAdapter
import com.theost.tike.common.recycler.element.event.EventAdapterDelegate
import com.theost.tike.common.recycler.element.friend.FriendAdapterDelegate
import com.theost.tike.common.recycler.element.title.TitleAdapterDelegate
import com.theost.tike.common.recycler.element.user.UserUi
import com.theost.tike.common.util.DisplayUtils
import com.theost.tike.core.deprecated.StateFragment
import com.theost.tike.databinding.FragmentInboxBinding
import com.theost.tike.domain.model.multi.EventAction.*
import com.theost.tike.domain.model.multi.EventMode
import com.theost.tike.domain.model.multi.FriendAction
import com.theost.tike.domain.model.multi.FriendAction.Block
import com.theost.tike.feature.inbox.presentation.InboxViewModel
import com.theost.tike.feature.inbox.ui.InboxFragmentDirections.Companion.actionInboxFragmentToInfoFragment
import com.theost.tike.feature.inbox.ui.InboxFragmentDirections.Companion.actionInboxFragmentToProfileFragment

class InboxFragment : StateFragment(R.layout.fragment_inbox) {

    private val adapter: BaseAdapter = BaseAdapter()

    private val viewModel: InboxViewModel by viewModels()
    private val binding: FragmentInboxBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.inboxList.adapter = adapter.apply {
            addDelegate(TitleAdapterDelegate())
            addDelegate(FriendAdapterDelegate { action ->
                when (action) {
                    is FriendAction.Accept -> viewModel.addFriend(action.id)
                    is FriendAction.Reject -> viewModel.deleteFriendRequest(action.id)
                    is Block -> viewModel.blockUser(action.id)
                    is FriendAction.Info -> findNavController().navigate(
                        actionInboxFragmentToProfileFragment(action.id)
                    )
                }
            })
            addDelegate(EventAdapterDelegate { action ->
                when (action) {
                    is Accept -> acceptEvent(
                        action.id,
                        action.creator,
                        action.participants,
                        action.mode
                    )
                    is Reject ->
                        DisplayUtils.showConfirmationDialog(
                            requireContext(),
                            R.string.ask_event_reject
                        ) {
                            rejectEvent(
                                action.id,
                                action.creator,
                                action.participants,
                                action.mode
                            )
                        }
                    is Info -> findNavController().navigate(
                        actionInboxFragmentToInfoFragment(action.id, action.creator)
                    )
                }
            })
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { handleStatus(it) }
        viewModel.items.observe(viewLifecycleOwner) { items ->
            binding.emptyView.isGone = items.isNotEmpty()
            adapter.submitList(items)
        }

        viewModel.init()
    }

    override fun bindState(): StateViews = StateViews(
        loadingView = binding.loadingView,
        errorView = binding.errorView
    )

    private fun acceptEvent(
        id: String,
        creator: String,
        participants: List<UserUi>,
        mode: EventMode
    ) {
        when (mode) {
            EventMode.PENDING_IN -> viewModel.addFromPendingInEvent(id, creator)
            EventMode.REQUESTING_IN -> participants.forEach { requesting ->
                viewModel.addFromRequestingInEvent(id, creator, requesting.uid)
            }
            else -> {}
        }
    }

    private fun rejectEvent(
        id: String,
        creator: String,
        participants: List<UserUi>,
        mode: EventMode
    ) {
        when (mode) {
            EventMode.REQUESTING_IN -> participants.forEach { requesting ->
                viewModel.deleteRequestingInEvent(id, requesting.uid)
            }
            EventMode.REQUESTING_OUT -> viewModel.deleteRequestingOutEvent(id, creator)
            EventMode.PENDING_IN -> viewModel.deletePendingInEvent(id, creator)
            EventMode.PENDING_OUT -> participants.forEach { requesting ->
                viewModel.deletePendingOutEvent(id, requesting.uid)
            }
            else -> {}
        }
    }
}
