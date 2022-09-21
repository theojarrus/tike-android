package com.theost.tike.common.extension

fun <T> fazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)