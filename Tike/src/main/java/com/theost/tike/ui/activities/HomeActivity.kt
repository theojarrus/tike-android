package com.theost.tike.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.theost.tike.R
import com.theost.tike.databinding.ActivityHomeBinding
import com.theost.tike.ui.interfaces.ActionsHolder
import com.theost.tike.ui.interfaces.CalendarHolder
import com.theost.tike.ui.interfaces.EventListener
import com.theost.tike.ui.interfaces.PeopleResultListener
import org.threeten.bp.LocalDate

class HomeActivity : FragmentActivity(), EventListener, ActionsHolder, CalendarHolder {

    private var activeDate: LocalDate? = null

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavController()
        setupSmoothBottomMenu()
    }

    override fun onEventCreated(date: LocalDate) {
        activeDate = date
        navController.navigateUp()
    }

    override fun getActiveDate(): LocalDate? {
        return activeDate?.run { LocalDate.from(activeDate).also { activeDate = null } }
    }

    override fun openParticipantsAdding(
        requestKey: String,
        bundleKey: String,
        addedIds: Set<String>,
        peopleResultListener: PeopleResultListener
    ) {
        supportFragmentManager.setFragmentResultListener(requestKey, this) { _, bundle ->
            peopleResultListener(bundle.getStringArrayList(bundleKey) ?: emptyList())
        }
        // todo
        // new Fragment.newInstance(addedIds)
        // setResult("requestKey", bundleOf("bundleKey" to "result"))
    }

    private fun setupNavController() {
        navHostFragment = (supportFragmentManager
            .findFragmentById(R.id.fragmentContainer) as NavHostFragment)
        navController = navHostFragment.navController
    }

    private fun setupSmoothBottomMenu() {
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.menu_bottom)
        binding.bottomBar.setupWithNavController(popupMenu.menu, navController)
    }

    companion object {
        fun newInstance(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }

}