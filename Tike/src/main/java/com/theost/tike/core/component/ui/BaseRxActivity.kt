package com.theost.tike.core.component.ui

import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseRxActivity(
    @LayoutRes contentLayoutId: Int
) : FragmentActivity(contentLayoutId) {

    private val compositeDisposable = CompositeDisposable()

    protected fun disposable(block: () -> Disposable) {
        compositeDisposable.add(block())
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
