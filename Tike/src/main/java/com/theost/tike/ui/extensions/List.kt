package com.theost.tike.ui.extensions

fun List<String>.append(value: String): List<String> {
    return toMutableList().apply { add(value) }.toList()
}

fun List<String>.append(value: List<String>): List<String> {
    return toMutableList().apply { addAll(value) }.toList()
}