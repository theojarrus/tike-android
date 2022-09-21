package com.theost.tike.common.extension

fun Int?.isNotLower(value: Int): Boolean {
    return this != null && this >= value
}