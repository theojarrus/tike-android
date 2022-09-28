package com.theost.tike.core.component.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.MenuItem.OnActionExpandListener
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.SearchView
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.core.component.presentation.BaseState
import com.theost.tike.core.component.presentation.BaseStateViewModel
import com.theost.tike.core.component.presentation.SearchViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers.io
import java.util.concurrent.TimeUnit.MILLISECONDS

abstract class BaseSearchStateFragment<State : BaseState, ViewModel : BaseStateViewModel<State>>(
    @LayoutRes contentLayoutId: Int
) : BaseStateFragment<State, ViewModel>(contentLayoutId) {

    protected abstract val searchMenuItem: MenuItem

    private val searchView: SearchView
        get() = searchMenuItem.actionView as SearchView

    private val searchViewModel: SearchViewModel
        get() = viewModel as SearchViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearch(searchMenuItem, searchView)
    }

    private fun setupSearch(searchItem: MenuItem, searchView: SearchView) {
        disposable {
            Observable.create<String> { emitter ->
                searchItem.setOnActionExpandListener(object : OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem): Boolean = true
                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        searchViewModel.completeSearch()
                        return true
                    }
                })
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = false
                    override fun onQueryTextChange(query: String): Boolean {
                        emitter.onNext(query)
                        return true
                    }
                })
            }.subscribeOn(io())
                .map { query -> query.trim().lowercase() }
                .distinctUntilChanged()
                .debounce(200, MILLISECONDS)
                .subscribe({ query ->
                    searchViewModel.search(query)
                }, { error ->
                    log(this, error)
                })
        }
    }
}
