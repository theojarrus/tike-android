package com.theost.tike.ui.fragments

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.theost.tike.R
import com.theost.tike.data.extensions.setNavigationResult
import com.theost.tike.data.models.state.Status
import com.theost.tike.databinding.FragmentParticipantsBinding
import com.theost.tike.ui.adapters.core.BaseAdapter
import com.theost.tike.ui.adapters.delegates.UserAdapterDelegate
import com.theost.tike.ui.viewmodels.ParticipantsViewModel

class ParticipantsFragment : Fragment() {

    private var selectedIds: List<String> = emptyList()

    private val adapter: BaseAdapter = BaseAdapter()

    private val viewModel: ParticipantsViewModel by viewModels()
    private val args: ParticipantsFragmentArgs by navArgs()

    private var _binding: FragmentParticipantsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentParticipantsBinding.inflate(layoutInflater, container, false)

        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        viewModel.participants.observe(viewLifecycleOwner) { users ->
            binding.emptyView.isGone = users.isNotEmpty()
            adapter.submitList(users)
        }

        viewModel.selectedIds.observe(viewLifecycleOwner) { ids ->
            selectedIds = ids
            binding.addParticipantsButton.isEnabled = ids.isNotEmpty()
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is Status.Error -> {
                    hideLoading()
                    showError()
                }
                Status.Loading -> {
                    hideError()
                    showLoading()
                }
                Status.Success -> hideLoading()
            }
        }

        binding.addParticipantsButton.setOnClickListener {
            binding.addParticipantsButton.isVisible = false
            setNavigationResult(args.requestKey, selectedIds)
            activity?.onBackPressed()
        }

        binding.reloadButton.setOnClickListener {
            viewModel.loadUsers(args.addedIds.toList())
        }

        binding.participantsList.adapter = adapter.apply {
            addDelegate(UserAdapterDelegate { userId, isSelected ->
                viewModel.onParticipantItemClicked(userId, isSelected)
            })
        }

        viewModel.loadUsers(args.addedIds.toList())

        configureToolbar()

        return binding.root
    }

    private fun configureToolbar() {
        val searchMenuItem = binding.toolbar.menu.findItem(R.id.menuSearch)
        val searchManager = context?.getSystemService(SEARCH_SERVICE) as SearchManager
        (searchMenuItem.actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            setIconifiedByDefault(true)
            viewModel.setupSearch(this)
        }
    }

    private fun showLoading() {
        binding.loadingBar.isGone = false
        binding.addParticipantsButton.visibility = View.INVISIBLE
    }

    private fun hideLoading() {
        binding.loadingBar.isGone = true
        binding.addParticipantsButton.visibility = View.VISIBLE
    }

    private fun showError() {
        binding.addParticipantsButton.visibility = View.INVISIBLE
        binding.reloadButton.isGone = false
        binding.errorView.isGone = false
        binding.emptyView.isGone = true
    }

    private fun hideError() {
        binding.addParticipantsButton.visibility = View.VISIBLE
        binding.reloadButton.isGone = true
        binding.errorView.isGone = true
        binding.emptyView.isGone = false
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}