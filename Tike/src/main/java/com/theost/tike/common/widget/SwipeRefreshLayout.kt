package com.theost.tike.common.widget

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.theost.tike.R
import com.theost.tike.common.util.ResUtils.getAttrColor

class SwipeRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs) {

    init {
        setColorSchemeColors(getAttrColor(context, R.attr.colorPrimary))
    }
}
