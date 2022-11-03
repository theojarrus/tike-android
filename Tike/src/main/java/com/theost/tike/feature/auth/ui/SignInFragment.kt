package com.theost.tike.feature.auth.ui

import android.app.Activity.RESULT_OK
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.util.AuthUtils.getCredential
import com.theost.tike.common.util.AuthUtils.getSignInIntent
import com.theost.tike.common.util.AuthUtils.getSignedInAccountFromIntent
import com.theost.tike.common.util.DisplayUtils.showError
import com.theost.tike.core.model.StateStatus.Initial
import com.theost.tike.core.model.StateStatus.Success
import com.theost.tike.core.model.StateViews
import com.theost.tike.core.ui.BaseStateFragment
import com.theost.tike.databinding.FragmentSignInBinding
import com.theost.tike.domain.model.multi.AuthStatus.SignedOut
import com.theost.tike.feature.auth.presentation.AuthViewModel
import com.theost.tike.feature.auth.presentation.SignInState
import com.theost.tike.feature.auth.presentation.SignInViewModel

class SignInFragment : BaseStateFragment<SignInState, SignInViewModel>(R.layout.fragment_sign_in) {

    private val authViewModel: AuthViewModel by activityViewModels()

    private val binding: FragmentSignInBinding by viewBinding()

    override val viewModel: SignInViewModel by viewModels()

    override val isHandlingState: Boolean = true
    override val isRefreshingErrorOnly: Boolean = true
    override val isLoadingEndless: Boolean = true

    private val authHandler = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) getSignedInAccountFromIntent(result)?.idToken
            ?.let { viewModel.signIn(getCredential(it)) }
            ?: showError(requireContext(), R.string.error_auth)
    }

    override fun setupView() = with(binding) {
        signIn.setOnClickListener { authHandler.launch(getSignInIntent(requireActivity())) }
    }

    override fun render(state: SignInState) {
        if (state.status == Success) authViewModel.updateAuthStatus(state.authStatus)
    }

    override val stateViews: StateViews
        get() = StateViews(
            actionView = binding.signIn,
            loadingView = binding.loadingView,
            errorMessage = getString(R.string.error_auth)
        )

    override val initialState: SignInState
        get() = SignInState(
            status = Initial,
            authStatus = SignedOut
        )

    override val initialAction: SignInViewModel.() -> Unit = {}

    companion object {

        fun newInstance(): SignInFragment {
            return SignInFragment()
        }
    }
}
