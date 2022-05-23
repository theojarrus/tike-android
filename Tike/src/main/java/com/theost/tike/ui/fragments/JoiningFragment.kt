package com.theost.tike.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.data.models.state.EventAction.Accept
import com.theost.tike.data.models.state.EventAction.Info
import com.theost.tike.databinding.FragmentJoiningBinding
import com.theost.tike.ui.adapters.core.BaseAdapter
import com.theost.tike.ui.adapters.delegates.EventAdapterDelegate
import com.theost.tike.ui.extensions.load
import com.theost.tike.ui.extensions.loadWithPlaceholder
import com.theost.tike.ui.fragments.AddingFragmentDirections.Companion.actionAddingFragmentToCreatorFragment
import com.theost.tike.ui.fragments.AddingFragmentDirections.Companion.actionAddingFragmentToInfoFragment
import com.theost.tike.ui.viewmodels.JoiningViewModel
import com.theost.tike.ui.viewmodels.MemberViewModel
import com.theost.tike.ui.widgets.StateFragment

class JoiningFragment : StateFragment(R.layout.fragment_joining) {

    private var isEmptyCreator = true

    private val adapter: BaseAdapter = BaseAdapter()

    private val memberViewModel: MemberViewModel by activityViewModels()
    private val viewModel: JoiningViewModel by viewModels()
    private val binding: FragmentJoiningBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.openCreatorButton.setOnClickListener { openCreatorAdding() }
        binding.emptyJoinView.emptyActionButton.setOnClickListener { openCreatorAdding() }
        binding.creatorView.removeParticipantButton.setOnClickListener {
            memberViewModel.setCreator(null)
            viewModel.clearCreator()
        }

        binding.emptyJoinView.emptyImage.load(R.drawable.empty_join)
        binding.eventsList.adapter = adapter.apply {
            addDelegate(EventAdapterDelegate { action ->
                when (action) {
                    is Accept -> viewModel.addEventRequest(action.id, action.creator)
                    is Info -> findNavController().navigate(
                        actionAddingFragmentToInfoFragment(
                            action.id,
                            action.creator
                        )
                    )
                    else -> {}
                }
            })
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { handleStatus(it) }
        viewModel.creator.observe(viewLifecycleOwner) { user ->
            with(binding) {
                isEmptyCreator = user == null
                emptyJoinView.root.isGone = !isEmptyCreator
                openCreatorButton.isInvisible = isEmptyCreator
                creatorView.apply {
                    root.isInvisible = isEmptyCreator
                    participantName.text = user?.name.orEmpty()
                    when (user?.hasAccess) {
                        true -> participantAvatar.loadWithPlaceholder(user.avatar, R.color.blue)
                        else -> participantAvatar.load(R.drawable.ic_blocked)
                    }
                }
            }
        }

        viewModel.events.observe(viewLifecycleOwner) { events ->
            binding.emptyView.isGone = events?.isEmpty() != true || isEmptyCreator
            binding.eventsTitle.isInvisible = events.isNullOrEmpty() || isEmptyCreator
            adapter.submitList(events ?: emptyList())
        }

        viewModel.init(memberViewModel.creator.value)
    }

    override fun bindState(): StateViews = StateViews(
        loadingView = binding.loadingBar,
        errorView = binding.errorView
    )

    private fun openCreatorAdding() {
        findNavController().navigate(actionAddingFragmentToCreatorFragment())
    }

    companion object {

        fun newInstance(): Fragment {
            return JoiningFragment()
        }
    }
}
