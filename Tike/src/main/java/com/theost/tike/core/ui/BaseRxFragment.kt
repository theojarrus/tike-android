package com.theost.tike.core.ui

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseRxFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    private val compositeDisposable = CompositeDisposable()

    protected fun disposable(block: () -> Disposable) {
        compositeDisposable.add(block())
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
