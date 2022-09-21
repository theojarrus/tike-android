package com.theost.tike.feature.preferences.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.core.screen.ToolbarStateFragment
import com.theost.tike.databinding.FragmentSettingsBinding
import com.theost.tike.domain.model.multi.AuthStatus
import com.theost.tike.feature.auth.ui.AuthActivity
import com.theost.tike.feature.preferences.presentation.PreferencesViewModel

class SettingsFragment : ToolbarStateFragment(R.layout.fragment_settings) {

    private val binding: FragmentSettingsBinding by viewBinding()
    private val viewModel: PreferencesViewModel by activityViewModels()
    private val args: SettingsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(true)

        viewModel.loadingStatus.observe(viewLifecycleOwner) { handleEndlessStatus(it) }
        viewModel.authStatus.observe(viewLifecycleOwner) { authStatus ->
            if (authStatus is AuthStatus.SignedOut) {
                startActivity(AuthActivity.newTaskIntent(requireContext()))
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PreferencesFragment.newInstance(args.id))
            .commit()
    }

    override fun bindState(): StateViews = StateViews(
        toolbar = binding.toolbar,
        actionView = binding.fragmentContainer,
        loadingView = binding.loadingBar.root,
        errorMessage = getString(R.string.error_unknown)
    )
}
