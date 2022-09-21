package com.theost.tike.common.extension

import com.androidhuman.rxfirebase2.firestore.model.Value

fun <T> Value<T>.getOrNull(): T? {
    return try {
        value()
    } catch (e: Exception) {
        null
    }
}