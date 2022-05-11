package com.theost.tike.ui.extensions

fun Int?.isNotLower(value: Int): Boolean {
    return this != null && this >= value
}