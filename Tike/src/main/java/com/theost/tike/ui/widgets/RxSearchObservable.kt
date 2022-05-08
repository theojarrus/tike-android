package com.theost.tike.ui.widgets

import androidx.appcompat.widget.SearchView
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

object RxSearchObservable {

    fun <T> fromView(
        searchView: SearchView,
        values: List<T>,
        filter: (T, String) -> Boolean
    ): Observable<List<T>> {
        return PublishSubject.create<String>()
            .apply {
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(s: String?): Boolean {
                        onComplete()
                        return true
                    }

                    override fun onQueryTextChange(query: String): Boolean {
                        onNext(query)
                        return true
                    }
                })
            }
            .map { query -> query.trim().lowercase() }
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS)
            .switchMapSingle { query ->
                when (query.isEmpty()) {
                    true -> Single.just(values)
                    false -> Observable.fromIterable(values)
                        .filter { filter(it, query) }
                        .toList()
                }
            }
            .subscribeOn(Schedulers.io())
    }
}