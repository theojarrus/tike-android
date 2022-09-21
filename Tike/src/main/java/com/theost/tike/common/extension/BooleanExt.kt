package com.theost.tike.common.extension

fun Boolean?.isTrue(): Boolean {
    return this == true
}

fun Boolean?.isFalse(): Boolean {
    return this == false
}