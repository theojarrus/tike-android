package com.theost.tike.ui.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            startHomeActivity()
        }, 500)
    }

    private fun startAuthActivity() {
        startActivity(AuthActivity.newInstance(this))
    }

    private fun startHomeActivity() {
        startActivity(HomeActivity.newInstance(this))
    }

}