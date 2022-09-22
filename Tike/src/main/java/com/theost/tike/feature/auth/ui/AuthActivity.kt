package com.theost.tike.feature.auth.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.extension.handleBackPress
import com.theost.tike.common.extension.pressBack
import com.theost.tike.databinding.ActivityAuthBinding
import com.theost.tike.domain.model.multi.AuthStatus
import com.theost.tike.domain.model.multi.AuthStatus.*
import com.theost.tike.domain.model.multi.Status.*
import com.theost.tike.domain.model.multi.getAuthStatusByName
import com.theost.tike.feature.auth.presentation.AuthViewModel
import com.theost.tike.feature.tike.TikeActivity

class AuthActivity : FragmentActivity(R.layout.activity_auth) {

    private val viewModel: AuthViewModel by viewModels()
    private val binding: ActivityAuthBinding by viewBinding(R.id.root)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        handleBackPress {
            when (viewModel.authStatus.value) {
                SigningUp -> viewModel.clearAuthData()
                else -> pressBack()
            }
        }

        viewModel.authStatus.observe(this) { authStatus ->
            when (authStatus) {
                SignedIn -> startTikeActivity(TikeActivity.newTaskIntent(this))
                SigningUp -> startFragment(SignUpFragment.newInstance())
                SignedOut -> startFragment(SignInFragment.newInstance())
            }
        }

        viewModel.loadingStatus.observe(this) { status ->
            when (status) {
                Loading -> showLoading()
                Success -> hideLoading()
                Error -> hideLoading()
            }
        }

        viewModel.loadAuthStatus(getAuthStatusByName(intent.getStringExtra(EXTRA_KEY_AUTH_STATUS)))
    }

    private fun showLoading() {
        binding.loadingBar.root.isGone = false
    }

    private fun hideLoading() {
        binding.loadingBar.root.isGone = true
    }

    private fun startFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.animator.nav_default_enter_anim,
                R.animator.nav_default_exit_anim,
                R.animator.nav_default_pop_enter_anim,
                R.animator.nav_default_pop_exit_anim
            )
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun startTikeActivity(intent: Intent) {
        startActivity(intent)
    }

    companion object {

        private const val EXTRA_KEY_AUTH_STATUS = "auth_status"

        fun newIntent(context: Context, authStatus: AuthStatus? = null): Intent {
            return Intent(context, AuthActivity::class.java).apply {
                authStatus?.let { putExtra(EXTRA_KEY_AUTH_STATUS, it.name) }
            }
        }

        fun newTaskIntent(context: Context, authStatus: AuthStatus? = null): Intent {
            return newIntent(context, authStatus).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
    }
}
