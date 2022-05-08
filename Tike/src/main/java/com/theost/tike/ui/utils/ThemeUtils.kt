package com.theost.tike.ui.utils

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES

object ThemeUtils {

    fun isDarkTheme(context: Context): Boolean {
        return when (context.resources.configuration.uiMode.minus(1)) {
            UI_MODE_NIGHT_NO -> false
            UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }
}