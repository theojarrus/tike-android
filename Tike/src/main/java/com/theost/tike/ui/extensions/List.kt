package com.theost.tike.ui.extensions

fun <T> List<T>.isNotEmptyNotEquals(value: List<T>): Boolean {
    return this != value && isNotEmpty() && value.isNotEmpty()
}

fun List<String>.append(value: String): List<String> {
    return toMutableList().apply { add(value) }.toList()
}
