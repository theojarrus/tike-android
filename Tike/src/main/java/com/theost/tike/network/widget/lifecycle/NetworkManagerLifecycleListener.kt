package com.theost.tike.network.widget.lifecycle

import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.theost.tike.network.widget.NetworkManager

class NetworkManagerLifecycleListener(
    private val networkManager: NetworkManager
) : LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Event) {
        when (event) {
            ON_START -> networkManager.start()
            ON_STOP -> networkManager.stop()
            else -> {}
        }
    }
}
