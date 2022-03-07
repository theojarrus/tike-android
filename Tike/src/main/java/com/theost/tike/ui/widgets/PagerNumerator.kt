package com.theost.tike.ui.widgets

class PagerNumerator {

    var pagerPosition: Int = 0

    private var pagerScrollDirection: PagerScrollDirection = PagerScrollDirection.SCROLL_STAY
    private var pagerTempPositionOffset: Float = 0f

    private fun updateScrollDirection(positionOffset: Float): Boolean {
        if (positionOffset > 0.1) {
            pagerTempPositionOffset = positionOffset
            pagerScrollDirection = if (positionOffset > 0.5) {
                PagerScrollDirection.SCROLL_PREV
            } else {
                PagerScrollDirection.SCROLL_NEXT
            }
        }
        return false
    }

    private fun calculateScroll(positionOffset: Float, calculateAction: () -> Boolean): Boolean {
        return if (positionOffset < 0.1) {
            pagerScrollDirection = PagerScrollDirection.SCROLL_STAY
            pagerTempPositionOffset = positionOffset
            false
        } else {
            calculateAction()
        }
    }

    private fun calculateScrollPrev(positionOffset: Float): Boolean {
        return calculateScroll(positionOffset) {
            if (pagerTempPositionOffset >= 0.5 && positionOffset < 0.5) {
                pagerTempPositionOffset = positionOffset
                pagerPosition -= 1
                true
            } else if (pagerTempPositionOffset < 0.5 && positionOffset >= 0.5) {
                pagerTempPositionOffset = positionOffset
                pagerPosition += 1
                true
            } else false
        }
    }

    private fun calculateScrollNext(positionOffset: Float): Boolean {
        return calculateScroll(positionOffset) {
            if (pagerTempPositionOffset <= 0.5 && positionOffset > 0.5) {
                pagerTempPositionOffset = positionOffset
                pagerPosition += 1
                true
            } else if (pagerTempPositionOffset > 0.5 && positionOffset <= 0.5) {
                pagerTempPositionOffset = positionOffset
                pagerPosition -= 1
                true
            } else false
        }
    }

    // Returns true if position was changed
    fun updatePosition(positionOffset: Float): Boolean {
        return when (pagerScrollDirection) {
            PagerScrollDirection.SCROLL_STAY -> updateScrollDirection(positionOffset)
            PagerScrollDirection.SCROLL_PREV -> calculateScrollPrev(positionOffset)
            PagerScrollDirection.SCROLL_NEXT -> calculateScrollNext(positionOffset)
        }
    }

    enum class PagerScrollDirection {
        SCROLL_STAY, SCROLL_PREV, SCROLL_NEXT
    }

}