package com.theost.tike.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.databinding.FragmentFriendsBinding
import com.theost.tike.ui.adapters.core.BaseAdapter
import com.theost.tike.ui.adapters.delegates.UserAdapterDelegate
import com.theost.tike.ui.fragments.FriendsFragmentDirections.Companion.actionFriendsFragmentToPeopleFragment
import com.theost.tike.ui.fragments.FriendsFragmentDirections.Companion.actionFriendsFragmentToProfileFragment
import com.theost.tike.ui.viewmodels.FriendsViewModel
import com.theost.tike.ui.widgets.SearchStateFragment

class FriendsFragment : SearchStateFragment(R.layout.fragment_friends) {

    private val adapter: BaseAdapter = BaseAdapter()

    private val viewModel: FriendsViewModel by viewModels()
    private val binding: FragmentFriendsBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchToolbar(true)

        viewModel.loadingStatus.observe(viewLifecycleOwner) { handleStatus(it) }
        viewModel.users.observe(viewLifecycleOwner) { users ->
            binding.emptyView.isGone = users.isNotEmpty()
            adapter.submitList(users)
        }

        binding.usersList.adapter = adapter.apply {
            addDelegate(UserAdapterDelegate() { uid ->
                findNavController().navigate(actionFriendsFragmentToProfileFragment(uid))
            })
        }

        binding.addFriendsButton.setOnClickListener {
            findNavController().navigate(actionFriendsFragmentToPeopleFragment())
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
