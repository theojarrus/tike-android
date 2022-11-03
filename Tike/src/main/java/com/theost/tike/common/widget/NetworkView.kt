package com.theost.tike.common.widget

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.Window
import androidx.annotation.ColorRes
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isGone
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.theost.tike.common.util.ThemeUtils.isDarkTheme

class NetworkView(
    private val context: Context,
    private val window: Window,
    private val rootView: ViewGroup,
    private val contentView: View,
    @ColorRes backgroundColor: Int
) {

    private val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
    private val windowTheme by lazy { isDarkTheme(context) }
    private val windowColor = window.statusBarColor

    private val widgetColor by lazy { context.getColor(backgroundColor) }
    private val widgetHeight by lazy { rootView.height.toFloat() }

    private val widgetInterpolator by lazy { FastOutSlowInInterpolator() }

    var isEnabled: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                update()
            }
        }

    private fun animate(view: View, animationDuration: Long): ViewPropertyAnimator {
        return view.animate().apply {
            interpolator = widgetInterpolator
            duration = animationDuration
        }
    }

    private fun animateWidget(view: View): ViewPropertyAnimator {
        return animate(view, ANIMATION_DURATION_WIDGET)
    }

    private fun animateContent(view: View): ViewPropertyAnimator {
        return animate(view, ANIMATION_DURATION_CONTENT)
    }

    private fun actionWidget(): () -> Unit = {
        rootView.isGone = !isEnabled
        window.statusBarColor = if (isEnabled) widgetColor else windowColor
        windowInsetsController.isAppearanceLightStatusBars = !(isEnabled || windowTheme)
    }

    private fun actionContent(alpha: Float): () -> Unit = {
        animateContent(contentView).alpha(alpha)
    }

    private fun animate(translationY: Float, startAction: () -> Unit, endAction: () -> Unit) {
        animateWidget(rootView).translationY(translationY)
            .withStartAction(startAction)
            .withEndAction(endAction)
    }

    private fun update() {
        if (isEnabled) {
            animate(0f, actionWidget(), actionContent(1f))
        } else {
            animate(-widgetHeight, actionContent(0f), actionWidget())
        }
    }

    companion object {

        private const val ANIMATION_DURATION_WIDGET = 2000L
        private const val ANIMATION_DURATION_CONTENT = 400L
    }
}
