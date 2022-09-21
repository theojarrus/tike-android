package com.theost.tike.feature.splash.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.theost.tike.R
import com.theost.tike.domain.model.multi.AuthStatus
import com.theost.tike.domain.model.multi.AuthStatus.SignedIn
import com.theost.tike.feature.auth.ui.AuthActivity
import com.theost.tike.feature.splash.presentation.SplashViewModel
import com.theost.tike.feature.tike.TikeActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(R.layout.activity_splash) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.authStatus.observe(this) { authStatus ->
            when (authStatus) {
                SignedIn -> startTikeActivity()
                else -> startAuthActivity(authStatus)
            }
        }

        viewModel.init()
    }

    private fun startAuthActivity(authStatus: AuthStatus) {
        startActivity(AuthActivity.newIntent(this, authStatus))
    }

    private fun startTikeActivity() {
        startActivity(TikeActivity.newIntent(this))
    }
}