package com.theost.tike.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.theost.tike.R
import com.theost.tike.data.api.FirestoreApi.SERVER_CLIENT_ID
import com.theost.tike.data.models.state.Status.Error
import com.theost.tike.data.models.state.Status.Loading
import com.theost.tike.data.models.state.Status.Success
import com.theost.tike.databinding.FragmentProfileBinding
import com.theost.tike.ui.extensions.load
import com.theost.tike.ui.interfaces.NavigationHolder
import com.theost.tike.ui.viewmodels.ProfileViewModel
import com.theost.tike.ui.widgets.ToolbarStateFragment

class ProfileFragment : ToolbarStateFragment(R.layout.fragment_profile) {

    private val reAuthHandler = registerForActivityResult(StartActivityForResult()) { onReAuth(it) }

    private val viewModel: ProfileViewModel by viewModels()
    private val binding: FragmentProfileBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.deleteButton.setOnClickListener { reAuthHandler.launch(getSignInIntent()) }
        binding.signOutButton.setOnClickListener { viewModel.signOut() }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { handleStatus(it) }
        viewModel.changingStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                Success -> (activity as? NavigationHolder)?.startAuthActivity()
                Loading -> {
                    (activity as? NavigationHolder)?.hideBottomNavigation()
                    binding.changingBar.isGone = false
                    binding.profileLayout.isGone = true
                }
                Error -> {
                    (activity as? NavigationHolder)?.showBottomNavigation()
                    binding.changingBar.isGone = true
                    binding.profileLayout.isGone = false
                }
            }
            handleStatus(status)
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

    private fun getSignInIntent(): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(SERVER_CLIENT_ID)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(requireActivity(), gso).signInIntent
    }

    private fun onReAuth(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            GoogleSignIn.getSignedInAccountFromIntent(result.data).result.idToken
                ?.let { viewModel.delete(GoogleAuthProvider.getCredential(it, null)) }
                ?: showErrorToast()
        }
    }

    override fun bindState(): StateViews = StateViews(
        toolbar = binding.toolbar,
        loadingView = binding.loadingBar,
        errorMessage = getString(R.string.error_unknown)
    )

    companion object {

        private const val ARG_PROFILE_UID = "profile_uid"

        fun newInstance(uid: String): Fragment {
            return ProfileFragment().apply { arguments = bundleOf(Pair(ARG_PROFILE_UID, uid)) }
        }
    }
}