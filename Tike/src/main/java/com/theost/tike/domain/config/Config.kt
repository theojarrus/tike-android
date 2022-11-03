package com.theost.tike.domain.config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.theost.tike.BuildConfig.DEBUG

object Config {

    val settings by lazy {
        remoteConfigSettings { minimumFetchIntervalInSeconds = if (DEBUG) 0L else 43200L }
    }

    val toggles by lazy { Toggle.values().associate { Pair(it.key, it.defaultValue) } }

    fun toggle(toggle: Toggle): Boolean {
        return Firebase.remoteConfig.getBoolean(toggle.key)
    }
}
