package com.theost.tike.core.component.model

import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.MaterialToolbar
import com.theost.tike.common.recycler.base.BaseAdapter

data class StateViews(
    /** Setup toolbar with navigation **/
    val toolbar: MaterialToolbar? = null,
    /** Setup loading view with state **/
    val swipeRefresh: SwipeRefreshLayout? = null,
    /** Setup keyboard with state **/
    val rootView: View? = null,
    /** Setup action view with state **/
    val actionView: View? = null,
    /** Setup loading view with state **/
    val loadingView: View? = null,
    /** Setup error view with state **/
    val errorView: View? = null,
    /** Setup error toast with state **/
    val errorMessage: String? = null,
    /** Setup recycler with state **/
    val disabledAdapter: BaseAdapter? = null,
    /** Setup views with state **/
    val disabledViews: List<View> = emptyList()
)
