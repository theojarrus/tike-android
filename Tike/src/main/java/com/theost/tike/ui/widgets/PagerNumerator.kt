package com.theost.tike.ui.widgets

class PagerNumerator {

    var pagerPosition: Int = 0

    private var pagerScrollDirection: PagerScrollDirection = PagerScrollDirection.SCROLL_STAY
    private var pagerTempPositionOffset: Float = 0f

    private fun updateScrollDirection(positionOffset: Float) {
        if (positionOffset > 0.1) {
            pagerTempPositionOffset = positionOffset
            pagerScrollDirection = if (positionOffset > 0.5) {
                PagerScrollDirection.SCROLL_PREV
            } else {
                PagerScrollDirection.SCROLL_NEXT
            }
        }
    }

    private fun calculateScroll(positionOffset: Float, calculateAction: () -> Unit) {
        if (positionOffset < 0.1) {
            pagerScrollDirection = PagerScrollDirection.SCROLL_STAY
            pagerTempPositionOffset = positionOffset
        } else {
            calculateAction.invoke()
        }
    }

    private fun calculateScrollPrev(positionOffset: Float) {
        calculateScroll(positionOffset) {
            if (pagerTempPositionOffset >= 0.5 && positionOffset < 0.5) {
                pagerTempPositionOffset = positionOffset
                pagerPosition -= 1
            } else if (pagerTempPositionOffset < 0.5 && positionOffset >= 0.5) {
                pagerTempPositionOffset = positionOffset
                pagerPosition += 1
            }
        }
    }

    private fun calculateScrollNext(positionOffset: Float) {
        calculateScroll(positionOffset) {
            if (pagerTempPositionOffset <= 0.5 && positionOffset > 0.5) {
                pagerTempPositionOffset = positionOffset
                pagerPosition += 1
            } else if (pagerTempPositionOffset > 0.5 && positionOffset <= 0.5) {
                pagerTempPositionOffset = positionOffset
                pagerPosition -= 1
            }
        }
    }

    fun updatePosition(positionOffset: Float) {
        when (pagerScrollDirection) {
            PagerScrollDirection.SCROLL_STAY -> updateScrollDirection(positionOffset)
            PagerScrollDirection.SCROLL_PREV -> calculateScrollPrev(positionOffset)
            PagerScrollDirection.SCROLL_NEXT -> calculateScrollNext(positionOffset)
        }
    }

    enum class PagerScrollDirection {
        SCROLL_STAY, SCROLL_PREV, SCROLL_NEXT
    }

}