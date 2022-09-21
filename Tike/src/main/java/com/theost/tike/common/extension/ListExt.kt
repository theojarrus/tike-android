package com.theost.tike.common.extension

fun List<String>.append(value: String): List<String> {
    return toMutableList().apply { add(value) }.toList()
}

fun List<String>.append(value: List<String>): List<String> {
    return toMutableList().apply { addAll(value) }.toList()
}

fun List<String>.mergeWith(vararg values: List<String>): List<String> {
    return toMutableList().apply { values.forEach { addAll(it) } }.toList()
}
