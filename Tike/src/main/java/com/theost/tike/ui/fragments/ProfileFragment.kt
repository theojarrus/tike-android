package com.theost.tike.ui.fragments

import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import android.view.View
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.databinding.FragmentProfileBinding
import com.theost.tike.ui.activities.PreferencesActivity
import com.theost.tike.ui.extensions.load
import com.theost.tike.ui.fragments.ProfileFragmentDirections.Companion.actionProfileFragmentToQrCodeFragment
import com.theost.tike.ui.viewmodels.ProfileViewModel
import com.theost.tike.ui.widgets.ToolbarStateFragment
import java.lang.String.format

class ProfileFragment : ToolbarStateFragment(R.layout.fragment_profile) {

    private var profileId: String? = null

    private val viewModel: ProfileViewModel by viewModels()
    private val binding: FragmentProfileBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileAddFriendButton.setOnClickListener {
            makeText(requireContext(), R.string.feature_not_ready, LENGTH_SHORT).show()
        }

        binding.profileRemoveFriendButton.setOnClickListener {
            makeText(requireContext(), R.string.feature_not_ready, LENGTH_SHORT).show()
        }

        binding.messageProfileButton.setOnClickListener {
            makeText(requireContext(), R.string.feature_not_ready, LENGTH_SHORT).show()
        }

        binding.profileShareButton.setOnClickListener { showProfileShare() }
        binding.profileQrCodeButton.setOnClickListener { showProfileQrCode() }
        binding.profileEditButton.setOnClickListener { editProfile() }
        binding.preferencesProfileButton.setOnClickListener { openPreferences() }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { handleStatus(it) }
        viewModel.user.observe(viewLifecycleOwner) { user ->
            profileId = user.nick
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

    override fun bindState(): StateViews = StateViews(
        toolbar = binding.toolbar,
        loadingView = binding.loadingBar,
        errorMessage = getString(R.string.error_unknown),
        disabledViews = listOf(
            binding.profileShareButton,
            binding.profileQrCodeButton,
            binding.profileEditButton,
            binding.friendsProfileButton,
            binding.preferencesProfileButton,
            binding.messageProfileButton,
            binding.profileAddFriendButton
        )
    )

    private fun setupCurrentUserProfile() {
        binding.profileCommunicateButtons.isGone = true
    }

    private fun setupOtherUserProfile() {
        with(binding) {
            setupToolbar(true)
            toolbar.isGone = false
            profileEditButton.isGone = true
            friendsProfileButton.isGone = true
            preferencesProfileButton.isGone = true
        }
    }

    private fun showProfileShare() {
        profileId?.let { id ->
            val intent = Intent().apply {
                action = ACTION_SEND
                type = "text/plain"
                putExtra(EXTRA_TITLE, getString(R.string.share))
                putExtra(EXTRA_TEXT, format(getString(R.string.share_profile), id))
            }
            startActivity(intent)
        }
    }

    private fun showProfileQrCode() {
        profileId?.let { id ->
            findNavController().navigate(
                actionProfileFragmentToQrCodeFragment(
                    format(getString(R.string.share_profile), id)
                )
            )
        }
    }

    private fun editProfile() {
        makeText(requireContext(), R.string.feature_not_ready, LENGTH_SHORT).show()
    }

    private fun openPreferences() {
        startActivity(PreferencesActivity.newIntent(requireContext(), profileId))
    }

    companion object {

        private const val ARG_PROFILE_UID = "profile_uid"

        fun newInstance(uid: String): Fragment {
            return ProfileFragment().apply { arguments = bundleOf(Pair(ARG_PROFILE_UID, uid)) }
        }
    }
}