package com.theost.tike.feature.auth.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.theost.tike.R
import com.theost.tike.common.exception.AuthException
import com.theost.tike.common.extension.getSerializable
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.domain.model.multi.AuthStatus
import com.theost.tike.domain.model.multi.AuthStatus.*
import com.theost.tike.feature.auth.presentation.AuthViewModel
import com.theost.tike.feature.tike.TikeActivity

class AuthActivity : FragmentActivity(R.layout.activity_auth) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialAuthStatus = intent.getSerializable(EXTRA_AUTH, AuthStatus::class.java, Unknown)
        viewModel.updateAuthStatus(initialAuthStatus)
        viewModel.authStatus.observe(this) { authStatus ->
            when (authStatus) {
                is SignedIn -> startActivity(TikeActivity.newTaskIntent(this))
                is SignedOut -> startFragment(SignInFragment.newInstance())
                is SigningUp -> startFragment(SignUpFragment.newInstance())
                is Unknown -> { log(this, AuthException()) }
            }
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
