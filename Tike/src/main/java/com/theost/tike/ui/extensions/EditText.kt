package com.theost.tike.ui.extensions

import android.widget.EditText

fun EditText.changeText(text: String) {
    setText(text)
    setSelection(text.length)
}