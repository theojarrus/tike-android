package com.theost.tike.ui.extensions

fun <T> fazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)