package com.theost.tike.ui.activities

import android.os.Bundle
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isGone
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.theost.tike.R
import com.theost.tike.data.models.state.AuthStatus
import com.theost.tike.data.models.state.Status
import com.theost.tike.databinding.ActivityTikeBinding
import com.theost.tike.ui.fragments.*
import com.theost.tike.ui.interfaces.AccountHolder
import com.theost.tike.ui.interfaces.ActionsHolder
import com.theost.tike.ui.interfaces.CalendarHolder
import com.theost.tike.ui.interfaces.EventListener
import com.theost.tike.ui.viewmodels.TikeViewModel
import org.threeten.bp.LocalDate

class TikeActivity : FragmentActivity(), EventListener, ActionsHolder, CalendarHolder, AccountHolder {

    private var pendingDate: LocalDate? = null

    private val viewModel: TikeViewModel by viewModels()

    private lateinit var binding: ActivityTikeBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition { viewModel.loadingStatus.value != Status.Success }
        }

        super.onCreate(savedInstanceState)
        binding = ActivityTikeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavController()
        setupSmoothBottomMenu()

        viewModel.authStatus.observe(this) { authStatus ->
            navController.currentDestination?.id?.let { id -> navigateAuth(id, authStatus) }
        }
    }

    private fun navigateAuth(fragmentId: Int, authStatus: AuthStatus) {
        when (fragmentId) {
            R.id.routingFragment -> {
                when (authStatus) {
                    AuthStatus.SignedIn -> navController.navigate(RoutingFragmentDirections.actionRoutingFragmentToScheduleFragment())
                    AuthStatus.SignedOut -> navController.navigate(RoutingFragmentDirections.actionRoutingFragmentToAuthFragment())
                    AuthStatus.SignedUp -> {
                        navController.navigate(RoutingFragmentDirections.actionRoutingFragmentToAuthFragment())
                        navController.navigate(AuthFragmentDirections.actionAuthFragmentToRegistrationFragment())
                    }
                }
            }
            R.id.authFragment -> {
                when (authStatus) {
                    AuthStatus.SignedIn -> navController.navigate(AuthFragmentDirections.actionAuthFragmentToScheduleFragment())
                    AuthStatus.SignedUp -> navController.navigate(AuthFragmentDirections.actionAuthFragmentToRegistrationFragment())
                    AuthStatus.SignedOut -> { /* do nothing */ }
                }
            }
            R.id.registrationFragment -> {
                when (authStatus) {
                    AuthStatus.SignedIn -> navController.navigate(RegistrationFragmentDirections.actionRegistrationFragmentToScheduleFragment())
                    AuthStatus.SignedUp -> { /* do nothing */ }
                    AuthStatus.SignedOut -> navController.navigateUp()
                }
            }
            else -> {
                when (authStatus) {
                    AuthStatus.SignedIn -> navController.navigate(R.id.scheduleFragment)
                    AuthStatus.SignedOut -> navController.navigate(R.id.authFragment)
                    AuthStatus.SignedUp -> {
                        navController.navigate(R.id.authFragment)
                        navController.navigate(AuthFragmentDirections.actionAuthFragmentToRegistrationFragment())
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.registrationFragment) deleteAccount() else super.onBackPressed()
    }

    private fun setupNavController() {
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment)
        navController = navHostFragment.navController
    }

    private fun setupSmoothBottomMenu() {
        val popupMenu = PopupMenu(this, binding.root).apply { inflate(R.menu.menu_bottom) }
        binding.bottomNavigation.setupWithNavController(popupMenu.menu, navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.isGone = popupMenu.menu.findItem(destination.id) == null
            if (destination.id == R.id.scheduleFragment || destination.id == R.id.authFragment) {
                navController.graph.setStartDestination(destination.id)
            }
        }
    }

    override fun openParticipantsAdding(requestKey: String, addedIds: List<String>) {
        navController.navigate(AddingFragmentDirections.actionAddingFragmentToParticipantsFragment(
            requestKey,
            addedIds.toTypedArray()
        ))
    }

    override fun onEventCreated(date: LocalDate) {
        pendingDate = date
        navController.navigateUp()
    }

    override fun getPendingDate(): LocalDate? {
        return pendingDate?.run { LocalDate.from(pendingDate).also { pendingDate = null } }
    }

    override fun deleteAccount() {
        viewModel.deleteAccount()
    }

    override fun signOut() {
        viewModel.signOut()
    }

}