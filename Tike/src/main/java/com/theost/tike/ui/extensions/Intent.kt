package com.theost.tike.ui.extensions

import android.content.Intent

fun Intent.newPlaintextShare(title: String, description: String): Intent {
    return apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, title)
        putExtra(Intent.EXTRA_TEXT, description)
    }
}
