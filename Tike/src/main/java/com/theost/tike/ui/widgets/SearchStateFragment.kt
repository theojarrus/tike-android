package com.theost.tike.ui.widgets

import android.app.SearchManager
import android.content.Context
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.SearchView
import com.theost.tike.R

abstract class SearchStateFragment(
    @LayoutRes contentLayoutId: Int
) : ToolbarStateFragment(contentLayoutId) {

    fun setupSearchToolbar(isBackEnabled: Boolean, setupSearchView: (SearchView) -> Unit) {
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
}