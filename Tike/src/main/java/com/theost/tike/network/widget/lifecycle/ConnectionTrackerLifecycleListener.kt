package com.theost.tike.network.widget.lifecycle

import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.theost.tike.network.widget.ConnectionTracker

class ConnectionTrackerLifecycleListener(
    private val networkTracker: ConnectionTracker
) : LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Event) {
        when (event) {
            ON_START -> networkTracker.start()
            ON_STOP -> networkTracker.stop()
            else -> {}
        }
    }
}
