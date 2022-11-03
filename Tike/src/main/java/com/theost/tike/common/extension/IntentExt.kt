package com.theost.tike.common.extension

import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import java.io.Serializable

inline fun <reified T : Serializable> Intent.getSerializable(key: String, clazz: Class<T>): T? {
    return if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
        getSerializableExtra(key, clazz)
    } else {
        @Suppress("DEPRECATION")
        getSerializableExtra(key) as? T
    }
}

inline fun <reified T : Serializable> Intent.getSerializable(
    key: String,
    clazz: Class<T>,
    defaultValue: T
): T {
    return getSerializable(key, clazz) ?: defaultValue
}

fun Intent.newPlaintextShare(title: String, description: String): Intent {
    return apply {
        action = ACTION_SEND
        type = "text/plain"
        putExtra(EXTRA_TITLE, title)
        putExtra(EXTRA_TEXT, description)
    }
}

fun Intent.newUrl(url: String): Intent {
    return apply {
        action = ACTION_VIEW
        data = Uri.parse(url)
    }
}
