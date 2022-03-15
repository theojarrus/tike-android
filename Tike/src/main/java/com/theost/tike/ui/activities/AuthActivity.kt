package com.theost.tike.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.theost.tike.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.authGoogleButton.setOnClickListener { startHomeActivity() }
    }

    private fun startHomeActivity() {
        startActivity(TikeActivity.newInstance(this))
    }

    companion object {
        fun newInstance(context: Context): Intent {
            return Intent(context, AuthActivity::class.java)
        }
    }

}