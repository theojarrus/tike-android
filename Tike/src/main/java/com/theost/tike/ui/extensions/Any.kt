package com.theost.tike.ui.extensions

fun Any?.toLongInt(): Int {
    return (this as? Long)?.toInt() ?: 0
}