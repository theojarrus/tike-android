package com.theost.tike.common.extension

import io.reactivex.Observable

fun <T, K> Observable<List<T>>.mapList(block: T.() -> K): Observable<List<K>> {
    return map { it.map(block) }
}
