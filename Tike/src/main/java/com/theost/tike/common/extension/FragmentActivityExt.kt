package com.theost.tike.common.extension

import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.FragmentActivity

fun FragmentActivity?.pressBack() {
    this?.onBackPressedDispatcher?.onBackPressed()
}

fun FragmentActivity?.handleBackPress(block: () -> Unit) {
    this?.onBackPressedDispatcher?.addCallback { block() }
}

fun FragmentActivity?.addBackPressedCallback(callback: OnBackPressedCallback) {
    this?.onBackPressedDispatcher?.addCallback(callback)
}
