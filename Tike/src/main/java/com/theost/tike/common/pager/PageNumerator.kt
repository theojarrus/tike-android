package com.theost.tike.common.pager

import com.theost.tike.common.pager.PageNumerator.PagerScrollDirection.*

typealias PageChangeListener = ((position: Int) -> Unit)

/** Custom ViewPager position counter that supports scrolling **/
/** Use calculatePosition(positionOffset: Int) inside OnPageChangeCallback::onPageScrolled  **/
class PageNumerator(var position: Int = 0, private val listener: PageChangeListener? = null) {

    private var scrollDirection: PagerScrollDirection = SCROLL_STAY
    private var tempPositionOffset: Float = 0f

    fun calculatePosition(positionOffset: Float) {
        when (scrollDirection) {
            SCROLL_STAY -> calculateScrollDirection(positionOffset)
            SCROLL_PREV -> calculateScrollPrev(positionOffset)
            SCROLL_NEXT -> calculateScrollNext(positionOffset)
        }
    }

    private fun calculateScrollDirection(positionOffset: Float) {
        if (positionOffset > 0.1) {
            tempPositionOffset = positionOffset
            scrollDirection = if (positionOffset > 0.5) SCROLL_PREV else SCROLL_NEXT
        }
    }

    private fun calculateScrollPrev(positionOffset: Float) {
        calculateScroll(
            positionOffset = positionOffset,
            isScrolledPrev = tempPositionOffset >= 0.5 && positionOffset < 0.5,
            isScrolledNext = tempPositionOffset < 0.5 && positionOffset >= 0.5
        )
    }

    private fun calculateScrollNext(positionOffset: Float) {
        calculateScroll(
            positionOffset = positionOffset,
            isScrolledPrev = tempPositionOffset > 0.5 && positionOffset <= 0.5,
            isScrolledNext = tempPositionOffset <= 0.5 && positionOffset > 0.5
        )
    }

    private fun calculateScroll(
        positionOffset: Float,
        isScrolledPrev: Boolean,
        isScrolledNext: Boolean
    ) {
        when {
            positionOffset < 0.1 -> fixPosition(positionOffset)
            isScrolledNext -> updatePosition(positionOffset, SCROLL_NEXT)
            isScrolledPrev -> updatePosition(positionOffset, SCROLL_PREV)
        }
    }

    private fun updatePosition(positionOffset: Float, direction: PagerScrollDirection) {
        tempPositionOffset = positionOffset
        position += direction.step
        listener?.invoke(position)
    }

    private fun fixPosition(positionOffset: Float) {
        tempPositionOffset = positionOffset
        scrollDirection = SCROLL_STAY
    }

    enum class PagerScrollDirection(val step: Int) {
        SCROLL_STAY(0),
        SCROLL_PREV(-1),
        SCROLL_NEXT(1)
    }
}