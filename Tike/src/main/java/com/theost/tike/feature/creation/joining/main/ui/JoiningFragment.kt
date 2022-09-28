package com.theost.tike.feature.creation.joining.main.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.extension.load
import com.theost.tike.common.extension.loadWithPlaceholder
import com.theost.tike.common.recycler.base.BaseAdapter
import com.theost.tike.common.recycler.element.event.EventAdapterDelegate
import com.theost.tike.common.util.DisplayUtils
import com.theost.tike.core.deprecated.StateFragment
import com.theost.tike.databinding.FragmentJoiningBinding
import com.theost.tike.domain.model.multi.EventAction.Accept
import com.theost.tike.domain.model.multi.EventAction.Info
import com.theost.tike.feature.creation.adding.members.presentation.MemberViewModel
import com.theost.tike.feature.creation.joining.main.presentation.JoiningViewModel
import com.theost.tike.feature.creation.main.ui.AddingFragmentDirections.Companion.actionAddingFragmentToCreatorFragment
import com.theost.tike.feature.creation.main.ui.AddingFragmentDirections.Companion.actionAddingFragmentToInfoFragment

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
                    is Accept -> DisplayUtils.showConfirmationDialog(
                        requireContext(),
                        R.string.ask_event_join
                    ) { viewModel.addEventRequest(action.id, action.creator) }
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
        loadingView = binding.loadingView,
        errorView = binding.errorView
    )

    private fun openCreatorAdding() {
        findNavController().navigate(actionAddingFragmentToCreatorFragment())
    }

    companion object {

        fun newInstance(): JoiningFragment {
            return JoiningFragment()
        }
    }
}
