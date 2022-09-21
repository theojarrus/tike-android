package com.theost.tike.common.extension

import android.widget.EditText

fun EditText.changeText(text: String) {
    setText(text)
    setSelection(text.length)
}