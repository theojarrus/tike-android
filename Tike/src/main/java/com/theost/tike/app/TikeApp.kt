package com.theost.tike.app

import android.app.Application
import android.net.ConnectivityManager
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.theost.tike.BuildConfig.DEBUG
import com.theost.tike.domain.config.Config.settings
import com.theost.tike.domain.config.Config.toggles
import com.theost.tike.network.widget.ConnectionTracker
import com.theost.tike.network.widget.NetworkManager
import com.theost.tike.network.widget.lifecycle.ConnectionTrackerLifecycleListener
import com.theost.tike.network.widget.lifecycle.NetworkManagerLifecycleListener

class TikeApp : Application() {

    private val connectionTracker by lazy {
        ConnectionTracker(getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager)
    }

    val networkManager by lazy {
        NetworkManager(connectionTracker)
    }

    override fun onCreate() {
        super.onCreate()
        setupCrashAnalytics()
        setupConfig()
        setupNetwork()
    }

    private fun setupCrashAnalytics() {
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!DEBUG)
    }

    private fun setupConfig() {
        Firebase.remoteConfig.apply {
            setConfigSettingsAsync(settings)
            setDefaultsAsync(toggles)
        }
    }

    private fun setupNetwork() {
        ProcessLifecycleOwner.get().lifecycle.apply {
            addObserver(ConnectionTrackerLifecycleListener(connectionTracker))
            addObserver(NetworkManagerLifecycleListener(networkManager))
        }
    }
}
