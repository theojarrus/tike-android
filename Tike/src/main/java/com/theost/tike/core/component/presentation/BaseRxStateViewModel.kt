package com.theost.tike.core.component.presentation

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseRxStateViewModel<State : BaseState> : BaseStateViewModel<State>() {

    protected val compositeDisposable by lazy { CompositeDisposable() }

    fun disposable(block: () -> Disposable) {
        compositeDisposable.add(block())
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
