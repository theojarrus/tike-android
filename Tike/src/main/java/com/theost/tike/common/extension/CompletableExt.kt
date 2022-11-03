package com.theost.tike.common.extension

import io.reactivex.Completable
import io.reactivex.disposables.Disposable

fun Completable.subscribe(onError: (Throwable) -> Unit): Disposable {
    return subscribe({}, onError)
}
