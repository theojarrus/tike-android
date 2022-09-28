package com.theost.tike.feature.splash.ui

import android.annotation.SuppressLint
import androidx.activity.viewModels
import com.theost.tike.R
import com.theost.tike.core.model.StateStatus.Initial
import com.theost.tike.core.model.StateViews
import com.theost.tike.core.ui.BaseStateActivity
import com.theost.tike.domain.model.multi.AuthStatus.SignedIn
import com.theost.tike.domain.model.multi.AuthStatus.Unknown
import com.theost.tike.feature.auth.ui.AuthActivity
import com.theost.tike.feature.splash.presentation.SplashState
import com.theost.tike.feature.splash.presentation.SplashViewModel
import com.theost.tike.feature.tike.TikeActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseStateActivity<SplashState, SplashViewModel>(R.layout.activity_splash) {

    override val viewModel: SplashViewModel by viewModels()

    override val isHandlingState: Boolean = false
    override val isLoadingEndless: Boolean = false
    override val isRefreshingErrorOnly: Boolean = false

    override fun setupView() {}

    override fun render(state: SplashState) {
        when (state.authStatus) {
            SignedIn -> startActivity(TikeActivity.newIntent(this))
            Unknown -> {}
            else -> startActivity(AuthActivity.newIntent(this, state.authStatus))
        }
    }

    override val stateViews: StateViews
        get() = StateViews()

    override val initialState: SplashState
        get() = SplashState(
            status = Initial,
            authStatus = Unknown
        )

    override val initialAction: SplashViewModel.() -> Unit = {
        fetchAuth()
    }
}
