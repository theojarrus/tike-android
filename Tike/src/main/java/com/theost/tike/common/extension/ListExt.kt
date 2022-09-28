package com.theost.tike.common.extension

fun List<String>.appendExcluding(value: String): List<String> {
    return toMutableList().apply { if (contains(value)) remove(value) else add(value) }.toList()
}

fun List<String>.append(value: String): List<String> {
    return toMutableList().apply { add(value) }.toList()
}

fun List<String>.append(value: List<String>): List<String> {
    return toMutableList().apply { addAll(value) }.toList()
}

fun List<String>.mergeWith(vararg values: List<String>): List<String> {
    return toMutableList().apply { values.forEach { addAll(it) } }.toList()
}

fun <T> List<T>.filterWith(value: String?, predicate: (String, T) -> Boolean): List<T> {
    return when {
        value.isNullOrEmpty() -> this
        else -> filter { predicate(value, it) }
    }
}
