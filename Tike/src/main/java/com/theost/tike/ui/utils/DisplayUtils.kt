package com.theost.tike.ui.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

object DisplayUtils {

    fun hideKeyboard(view: View) {
        (view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view.windowToken, 0)
    }
}