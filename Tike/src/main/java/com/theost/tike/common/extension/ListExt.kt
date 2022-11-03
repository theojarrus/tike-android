package com.theost.tike.common.extension

import kotlin.reflect.KProperty1

fun List<String>?.appendExcluding(value: String): List<String> {
    return this?.toMutableList()
        ?.apply { if (contains(value)) remove(value) else add(value) }
        ?.toList()
        .orEmpty()
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

fun <T> List<T>.filterNotWith(value: String?, predicate: (String, T) -> Boolean): List<T> {
    return when {
        value.isNullOrEmpty() -> this
        else -> filterNot { predicate(value, it) }
    }
}

inline fun <reified P, reified C : P> List<P>?.filterItem(predicate: (C) -> Boolean): List<P> {
    return this?.filter { it !is C || predicate(it) }.orEmpty()
}

inline fun <reified P, reified C : P> List<P>?.filterNotItem(predicate: (C) -> Boolean): List<P> {
    return this?.filterNot { it is C && predicate(it) }.orEmpty()
}

inline fun <reified P, reified C : P, K> List<P>?.filterItem(
    field: KProperty1<C, K>,
    key: K
): List<P> {
    return this?.filter { it !is C || field.get(it) != key }.orEmpty()
}

inline fun <reified P, reified C : P, K> List<P>?.filterNotItem(
    field: KProperty1<C, K>,
    key: K
): List<P> {
    return this?.filterNot { it is C && field.get(it) == key }.orEmpty()
}
