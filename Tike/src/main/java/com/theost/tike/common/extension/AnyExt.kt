package com.theost.tike.common.extension

fun Any?.toLongInt(): Int {
    return (this as? Long)?.toInt() ?: 0
}