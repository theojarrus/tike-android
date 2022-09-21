package com.theost.tike.common.extension

fun String.isLettersOnly(): Boolean {
    return all { it.isLetter() }
}