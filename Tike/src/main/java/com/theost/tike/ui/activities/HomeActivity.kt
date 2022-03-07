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

class HomeActivity : FragmentActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavController()
        setupSmoothBottomMenu()
    }

    private fun setupNavController() {
        navController = (supportFragmentManager
            .findFragmentById(R.id.fragmentContainer) as NavHostFragment)
            .navController
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