package com.theost.tike.feature.auth.ui

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.GoogleAuthProvider
import com.theost.tike.R
import com.theost.tike.common.util.AuthUtils.getSignInIntent
import com.theost.tike.core.screen.StateFragment
import com.theost.tike.databinding.FragmentSignInBinding
import com.theost.tike.feature.auth.presentation.SignInViewModel

class SignInFragment : StateFragment(R.layout.fragment_sign_in) {

    private val authHandler = registerForActivityResult(StartActivityForResult()) { onAuth(it) }

    private val viewModel: SignInViewModel by viewModels()
    private val binding: FragmentSignInBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.signingStatus.observe(viewLifecycleOwner) { handleEndlessStatus(it) }
        binding.signInButton.setOnClickListener {
            activity?.let { authHandler.launch(getSignInIntent(it)) }
        }
    }

    override fun bindState(): StateViews = StateViews(
        actionView = binding.signInButton,
        loadingView = binding.loadingBar,
        errorMessage = getString(R.string.error_auth)
    )

    private fun onAuth(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            GoogleSignIn.getSignedInAccountFromIntent(result.data).result.idToken
                ?.let { viewModel.signIn(GoogleAuthProvider.getCredential(it, null)) }
                ?: showErrorToast()
        }
    }

    companion object {

        fun newInstance(): Fragment {
            return SignInFragment()
        }
    }
}
