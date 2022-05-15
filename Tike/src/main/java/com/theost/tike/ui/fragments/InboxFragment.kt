package com.theost.tike.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.data.models.state.Action.*
import com.theost.tike.databinding.FragmentInboxBinding
import com.theost.tike.ui.adapters.core.BaseAdapter
import com.theost.tike.ui.adapters.delegates.FriendAdapterDelegate
import com.theost.tike.ui.adapters.delegates.TitleAdapterDelegate
import com.theost.tike.ui.fragments.InboxFragmentDirections.Companion.actionInboxFragmentToProfileFragment
import com.theost.tike.ui.viewmodels.InboxViewModel
import com.theost.tike.ui.widgets.StateFragment

class InboxFragment : StateFragment(R.layout.fragment_inbox) {

    private val adapter: BaseAdapter = BaseAdapter()

    private val viewModel: InboxViewModel by viewModels()
    private val binding: FragmentInboxBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.inboxList.adapter = adapter.apply {
            addDelegate(TitleAdapterDelegate())
            addDelegate(FriendAdapterDelegate() { action ->
                when (action) {
                    is Accept -> viewModel.addFriend(action.id)
                    is Reject -> viewModel.deleteFriendRequest(action.id)
                    is Block -> viewModel.blockUser(action.id)
                    is Info -> findNavController().navigate(
                        actionInboxFragmentToProfileFragment(action.id)
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
        loadingView = binding.loadingBar,
        errorView = binding.errorView
    )
}