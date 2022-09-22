package com.theost.tike.common.extension

import io.reactivex.Single

fun <T, K> Single<List<T>>.mapList(block: T.() -> K): Single<List<K>> {
    return map { it.map(block) }
}
