package com.theost.tike.common.extension

import android.view.MenuItem
import androidx.appcompat.widget.Toolbar

fun Toolbar.setSingleOnItemClickListener(block: (MenuItem) -> Unit) {
    setOnMenuItemClickListener {
        block(it)
        true
    }
}
