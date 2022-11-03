package com.theost.tike.common.extension

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import java.io.Serializable

inline fun <reified T : Serializable> Bundle.getSerializableExtra(
    key: String,
    clazz: Class<T>
): T? {
    return if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
        getSerializable(key, clazz)
    } else {
        @Suppress("DEPRECATION")
        getSerializable(key) as? T
    }
}

inline fun <reified T : Serializable> Bundle.getSerializableExtra(
    key: String,
    clazz: Class<T>,
    defaultValue: T
): T {
    return getSerializableExtra(key, clazz) ?: defaultValue
}
