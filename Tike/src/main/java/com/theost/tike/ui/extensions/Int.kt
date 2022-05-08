package com.theost.tike.ui.extensions

fun Int?.isPositive(): Boolean {
    return this != null && this >= 0
}