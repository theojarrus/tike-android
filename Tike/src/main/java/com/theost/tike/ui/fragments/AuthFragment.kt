package com.theost.tike.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.theost.tike.R
import com.theost.tike.data.models.state.Status
import com.theost.tike.databinding.FragmentAuthBinding
import com.theost.tike.ui.viewmodels.AuthViewModel

class AuthFragment : Fragment() {

    private val authorizationHandler =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                GoogleSignIn.getSignedInAccountFromIntent(result.data).result.idToken?.let { token ->
                    viewModel.signIn(token)
                } ?: showErrorToast()
            }
        }

    private val viewModel: AuthViewModel by viewModels()

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(layoutInflater, container, false)

        viewModel.loadingStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                Status.Loading -> showLoading()
                Status.Success -> hideLoading()
                Status.Error -> {
                    showErrorToast()
                    hideLoading()
                }
            }
        }

        binding.authUserButton.setOnClickListener { authorizationHandler.launch(getSignInIntent()) }

        viewModel.onViewLoaded()

        return binding.root
    }

    private fun getSignInIntent(): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(SERVER_CLIENT_ID)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        return mGoogleSignInClient.signInIntent
    }

    private fun showLoading() {
        binding.authUserButton.visibility = View.INVISIBLE
        binding.loadingBar.isGone = false
    }

    private fun hideLoading() {
        binding.authUserButton.visibility = View.VISIBLE
        binding.loadingBar.isGone = true
    }

    private fun showErrorToast() {
        Toast.makeText(context, R.string.error_auth, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val SERVER_CLIENT_ID =
            "694580831967-k64snkpmp1k6bb38bps6fifaoh7acq6e.apps.googleusercontent.com"
    }

}