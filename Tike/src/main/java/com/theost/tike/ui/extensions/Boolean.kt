package com.theost.tike.ui.extensions

fun Boolean?.isTrue(): Boolean {
    return this == true
}

fun Boolean?.isFalse(): Boolean {
    return this == false
}