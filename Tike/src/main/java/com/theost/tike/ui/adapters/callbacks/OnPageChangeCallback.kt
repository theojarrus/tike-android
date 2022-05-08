package com.theost.tike.ui.adapters.callbacks

import androidx.viewpager2.widget.ViewPager2

class OnPageChangeCallback(
    private val callback: (Float) -> Unit
) : ViewPager2.OnPageChangeCallback() {
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
        callback(positionOffset)
    }
}