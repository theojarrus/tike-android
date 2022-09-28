package com.theost.tike.common.extension

import io.reactivex.Observable

fun <T, K> Observable<List<T>>.mapList(block: T.() -> K): Observable<List<K>> {
    return map { it.map(block) }
}

fun <T> Observable<List<T>>.filterList(predicate: (T) -> Boolean): Observable<List<T>> {
    return map { it.filter(predicate) }
}
