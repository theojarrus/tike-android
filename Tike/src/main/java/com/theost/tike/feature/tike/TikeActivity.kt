package com.theost.tike.feature.tike

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.core.view.isInvisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.app.TikeApp
import com.theost.tike.common.widget.NetworkView
import com.theost.tike.core.model.StateStatus
import com.theost.tike.core.model.StateStatus.Initial
import com.theost.tike.core.model.StateStatus.Loading
import com.theost.tike.core.model.StateViews
import com.theost.tike.core.ui.BaseStateActivity
import com.theost.tike.databinding.ActivityTikeBinding
import com.theost.tike.domain.model.multi.AuthStatus.SignedIn
import com.theost.tike.feature.auth.presentation.AuthState
import com.theost.tike.feature.auth.presentation.AuthViewModel
import com.theost.tike.feature.auth.ui.AuthActivity
import com.theost.tike.network.model.multi.NetworkStatus.Online

class TikeActivity : BaseStateActivity<AuthState, AuthViewModel>(R.layout.activity_tike) {

    private val binding: ActivityTikeBinding by viewBinding(R.id.root)
    override val viewModel: AuthViewModel by viewModels()

    override val isHandlingState: Boolean = true
    override val isRefreshingErrorOnly: Boolean = true
    override val isLoadingEndless: Boolean = true

    private lateinit var navController: NavController
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
        setupNavController()
        setupSmoothBottomMenu(savedInstanceState?.getInt(KEY_NAVIGATION_STATE))
        setupNetworkView()
    }

    override fun render(state: AuthState) {
        if (state.authStatus != SignedIn) startActivity(AuthActivity.newTaskIntent(this))
    }

    override fun handleStatus(status: StateStatus) {
        super.handleStatus(status)
        binding.bottomNavigation.isInvisible = status == Loading
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_NAVIGATION_STATE, binding.bottomNavigation.itemActiveIndex)
    }

    private fun setupNavController() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        navController = (navHostFragment as NavHostFragment).navController
    }

    private fun setupSmoothBottomMenu(savedInstanceIndex: Int?) {
        val popupMenu = PopupMenu(this, binding.root).apply { inflate(R.menu.menu_bottom) }
        binding.bottomNavigation.setupWithNavController(popupMenu.menu, navController)
        savedInstanceIndex?.let { binding.bottomNavigation.itemActiveIndex = it }
    }

    private fun setupNetworkView() {
        networkManager.status.observe(this) { networkStatus ->
            when (networkStatus) {
                Online -> networkView.isEnabled = false
                else -> networkView.isEnabled = true
            }
        }
    }

    override val stateViews: StateViews
        get() = StateViews(
            actionView = binding.fragmentContainer,
            loadingView = binding.loadingView.root,
            errorMessage = getString(R.string.error_auth)
        )

    override val initialState: AuthState
        get() = AuthState(
            status = Initial,
            authStatus = SignedIn
        )

    override val initialAction: AuthViewModel.() -> Unit = {}

    companion object {

        private const val KEY_NAVIGATION_STATE = "navigation_state"

        fun newIntent(context: Context): Intent {
            return Intent(context, TikeActivity::class.java)
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
