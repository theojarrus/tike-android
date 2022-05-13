package com.theost.tike.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.databinding.FragmentParticipantsBinding
import com.theost.tike.ui.adapters.core.BaseAdapter
import com.theost.tike.ui.adapters.delegates.ParticipantAdapterDelegate
import com.theost.tike.ui.extensions.setNavigationResult
import com.theost.tike.ui.viewmodels.ParticipantsViewModel
import com.theost.tike.ui.widgets.SearchStateFragment
import java.lang.String.format

class ParticipantsFragment : SearchStateFragment(R.layout.fragment_participants) {

    private var selectedIds: List<String> = emptyList()

    private val adapter: BaseAdapter = BaseAdapter()

    private val viewModel: ParticipantsViewModel by viewModels()
    private val binding: FragmentParticipantsBinding by viewBinding()
    private val args: ParticipantsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchToolbar(true)

        viewModel.loadingStatus.observe(viewLifecycleOwner) { handleStatus(it) }

        viewModel.participants.observe(viewLifecycleOwner) { users ->
            binding.emptyView.isGone = users.isNotEmpty()
            adapter.submitList(users)
        }

        viewModel.selectedParticipants.observe(viewLifecycleOwner) { ids ->
            with(binding.addParticipantsButton) {
                text = format(getString(R.string.choose_participants), ids.size)
                isEnabled = ids.isNotEmpty()
            }
            selectedIds = ids
        }

        binding.participantsList.adapter = adapter.apply {
            addDelegate(ParticipantAdapterDelegate { uid -> viewModel.selectParticipant(uid) })
        }

        binding.addParticipantsButton.setOnClickListener {
            setNavigationResult(args.requestKey, selectedIds)
            findNavController().navigateUp()
        }

        viewModel.init(args.addedIds.toList())
    }

    override fun onSearch(query: String) = viewModel.searchParticipants(query)

    override fun bindState(): StateViews = StateViews(
        toolbar = binding.toolbar,
        actionView = binding.addParticipantsButton,
        loadingView = binding.loadingBar,
        errorView = binding.errorView,
        disabledAdapter = adapter
    )
}
