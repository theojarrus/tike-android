package com.theost.tike.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.data.models.state.Status.Success
import com.theost.tike.databinding.FragmentProfileBinding
import com.theost.tike.ui.extensions.load
import com.theost.tike.ui.interfaces.NavigationHolder
import com.theost.tike.ui.viewmodels.ProfileViewModel
import com.theost.tike.ui.widgets.ToolbarStateFragment

class ProfileFragment : ToolbarStateFragment(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by viewModels()
    private val binding: FragmentProfileBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signOutButton.setOnClickListener { viewModel.signOut() }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { handleStatus(it) }
        viewModel.signingOutStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                Success -> (activity as? NavigationHolder)?.startAuthActivity()
                else -> handleStatus(status)
            }
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.profileAvatar.load(user.avatar)
            binding.profileName.text = user.name
            binding.profileNick.text = user.nick
        }

        arguments?.getString(ARG_PROFILE_UID)?.let {
            setupOtherUserProfile()
            viewModel.init(it)
        } ?: let {
            setupCurrentUserProfile()
            viewModel.init()
        }
    }

    private fun setupCurrentUserProfile() {
        binding.messageProfileButton.isGone = true
    }

    private fun setupOtherUserProfile() {
        with(binding) {
            setupToolbar(true)
            toolbar.isGone = false
            profileEditButton.isGone = true
            friendsProfileButton.isGone = true
            settingsProfileButton.isGone = true
        }
    }

    override fun bindState(): StateViews = StateViews(
        actionView = binding.profileLayout,
        toolbar = binding.toolbar,
        loadingView = binding.loadingBar,
        errorMessage = getString(R.string.error_network)
    )

    companion object {

        private const val ARG_PROFILE_UID = "profile_uid"

        fun newInstance(uid: String): Fragment {
            return ProfileFragment().apply { arguments = bundleOf(Pair(ARG_PROFILE_UID, uid)) }
        }
    }
}