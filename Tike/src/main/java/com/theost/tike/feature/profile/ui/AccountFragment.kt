package com.theost.tike.feature.profile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.extension.load
import com.theost.tike.common.extension.newPlaintextShare
import com.theost.tike.core.deprecated.StateFragment
import com.theost.tike.databinding.FragmentAccountBinding
import com.theost.tike.feature.profile.presentation.AccountViewModel
import com.theost.tike.feature.profile.ui.AccountFragmentDirections.Companion.actionAccountFragmentToFriendsFragment
import com.theost.tike.feature.profile.ui.AccountFragmentDirections.Companion.actionAccountFragmentToQrCodeFragment
import com.theost.tike.feature.profile.ui.AccountFragmentDirections.Companion.actionAccountFragmentToSettingsFragment
import java.lang.String.format

class AccountFragment : StateFragment(R.layout.fragment_account) {

    private var accountId: String? = null

    private val viewModel: AccountViewModel by viewModels()
    private val binding: FragmentAccountBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.accountShareButton.setOnClickListener { showAccountShare() }
        binding.accountQrCodeButton.setOnClickListener { showAccountQrCode() }
        binding.accountEditButton.setOnClickListener { editAccount() }
        binding.friendsAccountButton.setOnClickListener { openFriends() }
        binding.preferencesAccountButton.setOnClickListener { openPreferences() }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { handleStatus(it) }
        viewModel.user.observe(viewLifecycleOwner) { user ->
            with(binding) {
                accountId = user.nick
                accountAvatar.load(user.avatar)
                accountName.text = user.name
                accountNick.text = user.nick
            }
        }
    }

    override fun bindState(): StateViews = StateViews(
        toolbar = binding.toolbar,
        loadingView = binding.loadingView.root,
        errorMessage = getString(R.string.error_unknown),
        disabledViews = listOf(
            binding.accountShareButton,
            binding.accountQrCodeButton,
            binding.accountEditButton,
            binding.friendsAccountButton,
            binding.preferencesAccountButton
        )
    )

    private fun showAccountShare() {
        accountId?.let { id ->
            startActivity(
                Intent().newPlaintextShare(
                    getString(R.string.share),
                    format(getString(R.string.share_profile), id)
                )
            )
        }
    }

    private fun showAccountQrCode() {
        accountId?.let { id ->
            findNavController().navigate(
                actionAccountFragmentToQrCodeFragment(
                    format(getString(R.string.share_account), id)
                )
            )
        }
    }

    private fun editAccount() {
        makeText(requireContext(), R.string.feature_not_ready, LENGTH_SHORT).show()
    }

    private fun openFriends() {
        findNavController().navigate(actionAccountFragmentToFriendsFragment())
    }

    private fun openPreferences() {
        accountId?.let { id ->
            findNavController().navigate(actionAccountFragmentToSettingsFragment(id))
        }
    }
}
