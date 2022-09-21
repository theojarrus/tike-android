package com.theost.tike.feature.people.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.recycler.base.BaseAdapter
import com.theost.tike.core.recycler.user.UserAdapterDelegate
import com.theost.tike.core.screen.SearchStateFragment
import com.theost.tike.databinding.FragmentPeopleBinding
import com.theost.tike.feature.people.presentation.PeopleViewModel
import com.theost.tike.feature.people.ui.PeopleFragmentDirections.Companion.actionPeopleFragmentToProfileFragment

class PeopleFragment : SearchStateFragment(R.layout.fragment_people) {

    private val adapter: BaseAdapter = BaseAdapter()

    private val viewModel: PeopleViewModel by viewModels()
    private val binding: FragmentPeopleBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchToolbar(true)

        viewModel.loadingStatus.observe(viewLifecycleOwner) { handleStatus(it) }
        viewModel.users.observe(viewLifecycleOwner) { users ->
            binding.emptyView.isGone = users.isNotEmpty()
            adapter.submitList(users)
        }

        binding.usersList.adapter = adapter.apply {
            addDelegate(UserAdapterDelegate { uid ->
                findNavController().navigate(actionPeopleFragmentToProfileFragment(uid))
            })
        }
    }

    override fun onSearch(query: String) = viewModel.searchUsers(query)

    override fun bindState(): StateViews = StateViews(
        toolbar = binding.toolbar,
        loadingView = binding.loadingBar,
        errorView = binding.errorView,
        disabledAdapter = adapter
    )
}
