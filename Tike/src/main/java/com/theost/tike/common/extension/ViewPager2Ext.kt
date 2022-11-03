package com.theost.tike.common.extension

import androidx.viewpager2.widget.ViewPager2
import com.theost.tike.common.pager.OnPageChangeCallback

fun ViewPager2.registerOnPageChangeCallback(callback: (Float) -> Unit) {
    registerOnPageChangeCallback(OnPageChangeCallback(callback))
}