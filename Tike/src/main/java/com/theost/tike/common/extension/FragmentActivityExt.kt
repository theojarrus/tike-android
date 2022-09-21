package com.theost.tike.common.extension

import androidx.activity.addCallback
import androidx.fragment.app.FragmentActivity

fun FragmentActivity?.pressBack() {
    this?.onBackPressedDispatcher?.onBackPressed()
}

fun FragmentActivity?.handleBackPress(block: () -> Unit) {
    this?.onBackPressedDispatcher?.addCallback { block() }
}
