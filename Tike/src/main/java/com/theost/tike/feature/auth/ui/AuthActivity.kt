package com.theost.tike.feature.auth.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.app.TikeApp
import com.theost.tike.common.exception.AuthException
import com.theost.tike.common.extension.getSerializable
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.common.widget.NetworkView
import com.theost.tike.core.model.StateStatus.Initial
import com.theost.tike.core.model.StateViews
import com.theost.tike.core.ui.BaseStateActivity
import com.theost.tike.databinding.ActivityAuthBinding
import com.theost.tike.domain.model.multi.AuthStatus
import com.theost.tike.domain.model.multi.AuthStatus.*
import com.theost.tike.feature.auth.presentation.AuthState
import com.theost.tike.feature.auth.presentation.AuthViewModel
import com.theost.tike.feature.tike.TikeActivity
import com.theost.tike.network.model.multi.NetworkStatus.Online

class AuthActivity : BaseStateActivity<AuthState, AuthViewModel>(R.layout.activity_auth) {

    private val binding: ActivityAuthBinding by viewBinding()

    override val viewModel: AuthViewModel by viewModels()

    override val isHandlingState: Boolean = true
    override val isRefreshingErrorOnly: Boolean = true

    private val networkManager by lazy { (application as TikeApp).networkManager }
    private val networkView by lazy {
        NetworkView(
            context = this,
            window = window,
            rootView = binding.networkView.root,
            contentView = binding.networkView.content,
            backgroundColor = R.color.red
        )
    }

    override fun setupView(savedInstanceState: Bundle?) {
        networkManager.status.observe(this) { networkStatus ->
            when (networkStatus) {
                Online -> networkView.isEnabled = false
                else -> networkView.isEnabled = true
            }
        }
    }

    override fun render(state: AuthState) {
        when (state.authStatus) {
            is SignedIn -> startActivity(TikeActivity.newTaskIntent(this))
            is SignedOut -> startFragment(SignInFragment.newInstance())
            is SigningUp -> startFragment(SignUpFragment.newInstance())
            is Unknown -> { log(this, AuthException()) }
        }
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

    override val stateViews: StateViews
        get() = StateViews(
            loadingView = binding.loadingView.root,
            errorMessage = getString(R.string.error_auth)
        )

    override val initialState: AuthState
        get() = AuthState(
            status = Initial,
            authStatus = intent.getSerializable(EXTRA_AUTH, AuthStatus::class.java, Unknown)
        )

    override val initialAction: AuthViewModel.() -> Unit = {}

    companion object {

        private const val EXTRA_AUTH = "auth_status"

        fun newIntent(context: Context, auth: AuthStatus = SignedOut): Intent {
            return Intent(context, AuthActivity::class.java).apply {
                putExtra(EXTRA_AUTH, auth)
            }
        }

        fun newTaskIntent(context: Context): Intent {
            return newIntent(context).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
    }
}
