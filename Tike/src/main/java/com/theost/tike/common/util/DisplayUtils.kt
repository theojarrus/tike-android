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

    fun showError(context: Context?, @StringRes message: Int) {
        context?.let { makeText(context, message, LENGTH_SHORT).show() }
    }

    fun showError(context: Context?, message: String) {
        context?.let { makeText(context, message, LENGTH_SHORT).show() }
    }

    fun showDialog(
        context: Context,
        @StringRes titleResId: Int,
        @StringRes messageResId: Int,
        @StringRes cancelTextResId: Int,
        @StringRes confirmTextResId: Int,
        cancelAction: () -> Unit = {},
        confirmAction: () -> Unit = {}
    ) {
        AlertDialog.Builder(context)
            .setTitle(titleResId)
            .setMessage(messageResId)
            .setNegativeButton(cancelTextResId) { _, _ -> cancelAction() }
            .setPositiveButton(confirmTextResId) { _, _ -> confirmAction() }
            .create()
            .show()
    }

    fun showConfirmationDialog(context: Context, @StringRes messageResId: Int, action: () -> Unit) {
        showDialog(
            context = context,
            titleResId = R.string.confirmation,
            messageResId = messageResId,
            cancelTextResId = R.string.no,
            confirmTextResId = R.string.yes,
            confirmAction = action
        )
    }
}
