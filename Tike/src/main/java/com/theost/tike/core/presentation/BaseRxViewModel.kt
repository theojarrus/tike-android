package com.theost.tike.core.presentation

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseRxViewModel : ViewModel() {

    private val compositeDisposable by lazy { CompositeDisposable() }

    private val switchDisposable by lazy { CompositeDisposable() }

    protected fun disposable(block: () -> Disposable) {
        compositeDisposable.add(block())
    }

    protected fun disposableSwitch(block: () -> Disposable) {
        switchDisposable.clear()
        switchDisposable.add(block())
    }

    protected fun dispose() {
        compositeDisposable.clear()
        switchDisposable.clear()
    }

    override fun onCleared() {
        super.onCleared()
        dispose()
    }
}
