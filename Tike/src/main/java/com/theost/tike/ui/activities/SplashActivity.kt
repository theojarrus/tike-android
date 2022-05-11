package com.theost.tike.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.theost.tike.R
import com.theost.tike.data.models.state.AuthStatus
import com.theost.tike.data.models.state.AuthStatus.SignedIn
import com.theost.tike.ui.viewmodels.SplashViewModel

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