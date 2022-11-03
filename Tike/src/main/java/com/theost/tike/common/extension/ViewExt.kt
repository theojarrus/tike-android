package com.theost.tike.common.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

inline fun <reified T : View> View.inflate(
    @LayoutRes layout: Int,
    root: ViewGroup? = this as? ViewGroup,
    attachToRoot: Boolean = false
): T {
    return LayoutInflater.from(context).inflate(layout, root, attachToRoot) as T
}

fun View.setOnClickListener(isEnabled: Boolean, block: (View) -> Unit) {
    if (isEnabled) setOnClickListener(block) else setOnClickListener(null)
}
