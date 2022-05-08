package com.theost.tike.ui.extensions

import com.androidhuman.rxfirebase2.firestore.model.Value
import java.lang.Exception

fun <T> Value<T>.getOrNull(): T? {
    return try {
        value()
    } catch (e: Exception) {
        null
    }
}