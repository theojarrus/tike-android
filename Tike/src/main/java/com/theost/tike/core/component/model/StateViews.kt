package com.theost.tike.core.component.model

import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.MaterialToolbar
import com.theost.tike.common.recycler.base.BaseAdapter

data class StateViews(
    val swipeRefresh: SwipeRefreshLayout? = null,
    val rootView: View? = null,
    val actionView: View? = null,
    val loadingView: View? = null,
    val errorView: View? = null,
    val errorMessage: String? = null,
    val disabledAdapter: BaseAdapter? = null,
    val disabledViews: List<View> = emptyList()
)
