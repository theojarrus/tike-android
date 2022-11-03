package com.theost.tike.feature.splash.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.theost.tike.R
import com.theost.tike.app.TikeApp
import com.theost.tike.common.extension.newUrl
import com.theost.tike.common.util.DisplayUtils.showDialog
import com.theost.tike.core.model.StateStatus.Initial
import com.theost.tike.core.model.StateViews
import com.theost.tike.core.ui.BaseStateActivity
import com.theost.tike.domain.model.multi.AuthStatus.SignedIn
import com.theost.tike.domain.model.multi.AuthStatus.Unknown
import com.theost.tike.feature.auth.ui.AuthActivity
import com.theost.tike.feature.splash.presentation.SplashState
import com.theost.tike.feature.splash.presentation.SplashViewModel
import com.theost.tike.feature.tike.TikeActivity
import com.theost.tike.network.model.multi.NetworkStatus.Unsupported

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseStateActivity<SplashState, SplashViewModel>(R.layout.activity_splash) {

    override val viewModel: SplashViewModel by viewModels()

    override val isHandlingState: Boolean = false
    override val isRefreshingErrorOnly: Boolean = true

    private val networkManager by lazy { (application as TikeApp).networkManager }

    override fun setupView(savedInstanceState: Bundle?) {
        networkManager.status.observe(this) { networkStatus ->
            when (networkStatus) {
                Unsupported -> showUpdateDialog()
                else -> viewModel.fetchAuth()
            }
        }
    }

    override fun render(state: SplashState) {
        when (state.authStatus) {
            SignedIn -> startActivity(TikeActivity.newIntent(this))
            Unknown -> {}
            else -> startActivity(AuthActivity.newIntent(this, state.authStatus))
        }
    }

    private fun showUpdateDialog() {
        showDialog(
            context = this,
            titleResId = R.string.update_app,
            messageResId = R.string.update_app_message,
            cancelTextResId = R.string.not_now,
            confirmTextResId = R.string.update,
            cancelAction = { viewModel.fetchAuth() },
            confirmAction = { startActivity(Intent().newUrl(getString(R.string.app_releases))) }
        )
    }

    override val stateViews: StateViews
        get() = StateViews()

    override val initialState: SplashState
        get() = SplashState(
            status = Initial,
            authStatus = Unknown
        )

    override val initialAction: SplashViewModel.() -> Unit = {}
}
