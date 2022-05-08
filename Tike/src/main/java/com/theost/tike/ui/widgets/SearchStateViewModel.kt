package com.theost.tike.ui.widgets

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class SearchStateViewModel : ViewModel() {

    protected abstract fun bindSearchData(): List<Any>
    protected abstract fun bindSearchFilter(): (Any, String) -> Boolean
    protected abstract fun onDataLoaded(items: List<Any>)
    protected abstract fun onDataLoadError(error: Throwable)

    private val compositeDisposable = CompositeDisposable()

    fun setupSearch(searchView: SearchView) {
        compositeDisposable.add(
            RxSearchObservable.fromView(searchView, bindSearchData(), bindSearchFilter())
                .subscribe({ onDataLoaded(it) }, { onDataLoadError(it) })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}