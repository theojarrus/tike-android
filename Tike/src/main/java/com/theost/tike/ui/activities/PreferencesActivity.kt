package com.theost.tike.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.data.models.state.AuthStatus.SignedOut
import com.theost.tike.data.models.state.Status.*
import com.theost.tike.databinding.ActivityPreferencesBinding
import com.theost.tike.ui.fragments.PreferencesFragment
import com.theost.tike.ui.viewmodels.PreferencesViewModel

class PreferencesActivity : AppCompatActivity(R.layout.activity_preferences) {

    private val viewModel: PreferencesViewModel by viewModels()
    private val binding: ActivityPreferencesBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

    }

    private fun showLoading() {
        binding.loadingBar.isGone = false
        binding.fragmentContainer.isGone = true
    }

    private fun hideLoading() {
        binding.loadingBar.isGone = true
        binding.fragmentContainer.isGone = false
    }

    companion object {

        private const val EXTRA_ID = "id"

        fun newIntent(context: Context, id: String? = null): Intent {
            return Intent(context, PreferencesActivity::class.java).apply {
                putExtra(EXTRA_ID, id)
            }
        }
    }
}
