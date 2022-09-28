package com.theost.tike.core.component.presentation

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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        switchDisposable.clear()
    }
}
