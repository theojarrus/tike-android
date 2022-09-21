package com.theost.tike.core.screen

import android.app.SearchManager
import android.content.Context
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.SearchView
import com.theost.tike.R
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

abstract class SearchStateFragment(
    @LayoutRes contentLayoutId: Int
) : ToolbarStateFragment(contentLayoutId) {

    private val compositeDisposable = CompositeDisposable()

    protected abstract fun onSearch(query: String)

    fun setupSearchToolbar(isBackEnabled: Boolean) {
        setupToolbar(isBackEnabled)
        with(bindState()) {
            val searchMenuItem = toolbar?.menu?.findItem(R.id.menuSearch)
            val searchManager = context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            (searchMenuItem?.actionView as? SearchView)?.apply {
                setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
                setIconifiedByDefault(true)
                setupSearchView(this)
            }
        }
    }

    private fun setupSearchView(searchView: SearchView) {
        compositeDisposable.add(
            Observable.create<String> { emitter ->
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(s: String?): Boolean {
                        emitter.onComplete()
                        return true
                    }

                    override fun onQueryTextChange(query: String): Boolean {
                        emitter.onNext(query)
                        return true
                    }
                })
            }.map { query -> query.trim().lowercase() }
                .distinctUntilChanged()
                .debounce(500, TimeUnit.MILLISECONDS)
                .doOnNext { onSearch(it) }
                .subscribeOn(Schedulers.io())
                .subscribe({}, { it.printStackTrace() })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
