package com.theost.tike.ui.extensions

fun String.isLettersOnly(): Boolean {
    return all { it.isLetter() }
}