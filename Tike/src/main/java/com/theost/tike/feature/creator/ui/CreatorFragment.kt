package com.theost.tike.feature.creator.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.recycler.base.BaseAdapter
import com.theost.tike.common.recycler.element.member.MemberAdapterDelegate
import com.theost.tike.core.deprecated.SearchStateFragment
import com.theost.tike.databinding.FragmentCreatorBinding
import com.theost.tike.feature.creator.presentation.CreatorViewModel
import com.theost.tike.feature.members.presentation.MemberViewModel

class CreatorFragment : SearchStateFragment(R.layout.fragment_creator) {

    private var selectedId: String? = null

    private val adapter: BaseAdapter = BaseAdapter()

    private val memberViewModel: MemberViewModel by activityViewModels()
    private val viewModel: CreatorViewModel by viewModels()
    private val binding: FragmentCreatorBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchToolbar(true)

        viewModel.loadingStatus.observe(viewLifecycleOwner) { handleStatus(it) }

        viewModel.participants.observe(viewLifecycleOwner) { users ->
            binding.emptyView.isGone = users.isNotEmpty()
            adapter.submitList(users)
        }

        viewModel.selectedId.observe(viewLifecycleOwner) { id ->
            binding.addParticipantsButton.isEnabled = !id.isNullOrEmpty()
            selectedId = id
        }

        binding.participantsList.adapter = adapter.apply {
            addDelegate(MemberAdapterDelegate { uid -> viewModel.selectParticipant(uid) })
        }

        binding.addParticipantsButton.setOnClickListener {
            memberViewModel.setCreator(selectedId)
            findNavController().navigateUp()
        }

        viewModel.init(memberViewModel.creator.value)
    }

    override fun onSearch(query: String) = viewModel.searchParticipants(query)

    override fun bindState(): StateViews = StateViews(
        toolbar = binding.toolbar,
        loadingView = binding.loadingView,
        errorView = binding.errorView,
        disabledAdapter = adapter
    )
}
