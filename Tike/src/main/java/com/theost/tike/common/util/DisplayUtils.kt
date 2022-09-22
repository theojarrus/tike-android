package com.theost.tike.common.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.theost.tike.R

object DisplayUtils {

    fun hideKeyboard(view: View) {
        (view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showConfirmationDialog(context: Context, messageResId: Int, action: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle(R.string.confirmation)
            .setMessage(messageResId)
            .setPositiveButton(R.string.yes) { _, _ -> action() }
            .setNegativeButton(R.string.no) { _, _ -> }
            .create()
            .show()
    }

    fun showError(context: Context?, @StringRes message: Int) {
        context?.let { makeText(context, message, LENGTH_SHORT).show() }
    }
}
