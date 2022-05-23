package com.theost.tike.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.data.models.state.FriendStatus
import com.theost.tike.data.models.state.FriendStatus.*
import com.theost.tike.databinding.FragmentProfileBinding
import com.theost.tike.ui.extensions.load
import com.theost.tike.ui.extensions.loadWithFadeIn
import com.theost.tike.ui.extensions.newPlaintextShare
import com.theost.tike.ui.fragments.ProfileFragmentDirections.Companion.actionProfileFragmentToQrCodeFragment
import com.theost.tike.ui.utils.DisplayUtils.showConfirmationDialog
import com.theost.tike.ui.viewmodels.ProfileViewModel
import com.theost.tike.ui.widgets.ToolbarStateFragment
import java.lang.String.format

class ProfileFragment : ToolbarStateFragment(R.layout.fragment_profile) {

    private var profileId: String? = null

    private val args: ProfileFragmentArgs by navArgs()
    private val viewModel: ProfileViewModel by viewModels()
    private val binding: FragmentProfileBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(true)

        binding.messageProfileButton.setOnClickListener {
            makeText(requireContext(), R.string.feature_not_ready, LENGTH_SHORT).show()
        }

        binding.profileAddFriendButton.setOnClickListener { viewModel.addFriend() }
        binding.profileAddFriendRequestButton.setOnClickListener { viewModel.addFriendRequest() }
        binding.profilePendingFriendButton.setOnClickListener { viewModel.cancelPending() }
        binding.profileRemoveFriendButton.setOnClickListener {
            showConfirmationDialog(
                requireContext(),
                R.string.ask_friend_delete
            ) { viewModel.deleteFriend() }
        }

        binding.profileShareButton.setOnClickListener { showProfileShare() }
        binding.profileQrCodeButton.setOnClickListener { showProfileQrCode() }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { handleStatus(it) }
        viewModel.user.observe(viewLifecycleOwner) { user ->
            with(binding) {
                setupToolbarMenu(user.isBlocked)
                profileId = user.nick
                profileName.text = user.name
                profileNick.text = user.nick
                blockedView.isGone = !user.isBlocked
                accessView.isGone = user.hasAccess
                when (user.hasAccess) {
                    true -> setupProfile(user.avatar, user.isBlocked, user.friendStatus)
                    false -> setupClosedProfile()
                }
            }
        }

        viewModel.init(args.uid)
    }

    override fun bindState(): StateViews = StateViews(
        toolbar = binding.toolbar,
        loadingView = binding.loadingBar.root,
        errorView = binding.errorView,
        disabledViews = listOf(
            binding.profileShareButton,
            binding.profileQrCodeButton,
            binding.messageProfileButton,
            binding.profileAddFriendButton,
            binding.profileAddFriendRequestButton,
            binding.profileRemoveFriendButton,
            binding.profilePendingFriendButton
        )
    )

    private fun setupToolbarMenu(isBlocked: Boolean) {
        with(binding.toolbar.menu) {
            findItem(R.id.profileUnblock).apply {
                isVisible = isBlocked
                setOnMenuItemClickListener {
                    viewModel.unblockUser()
                    true
                }
            }
            findItem(R.id.profileBlock).apply {
                isVisible = !isBlocked
                setOnMenuItemClickListener {
                    showConfirmationDialog(
                        requireContext(),
                        R.string.ask_user_block
                    ) { viewModel.blockUser() }
                    true
                }
            }
        }
    }

    private fun setupProfile(
        avatar: String,
        isBlocked: Boolean,
        friendStatus: FriendStatus
    ) {
        with(binding) {
            profileAvatar.load(avatar)
            profileActionsButtons.isInvisible = false
            profileCommunicateButtons.isInvisible = isBlocked
            profileAddFriendButton.isGone = friendStatus != PENDING
            profileRemoveFriendButton.isGone = friendStatus != FRIEND
            profilePendingFriendButton.isGone = friendStatus != REQUESTING
            profileAddFriendRequestButton.isGone = friendStatus != NOT_FRIEND
        }
    }

    private fun setupClosedProfile() {
        with(binding) {
            profileAvatar.loadWithFadeIn(R.drawable.ic_blocked)
            profileCommunicateButtons.isInvisible = true
            profileActionsButtons.isInvisible = true
        }
    }

    private fun showProfileShare() {
        profileId?.let { id ->
            startActivity(
                Intent().newPlaintextShare(
                    getString(R.string.share),
                    format(getString(R.string.share_profile), id)
                )
            )
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
}
