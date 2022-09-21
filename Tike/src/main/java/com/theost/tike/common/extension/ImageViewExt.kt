package com.theost.tike.common.extension

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.theost.tike.common.util.QrUtils.generateBitmap

fun ImageView.load(@DrawableRes drawableRes: Int) {
    Glide.with(this)
        .load(drawableRes)
        .placeholder(drawableRes)
        .transition(withCrossFade(300))
        .into(this)
}

fun ImageView.load(url: String) {
    Glide.with(this)
        .load(url)
        .transition(withCrossFade(300))
        .into(this)
}

fun ImageView.load(bitmap: Bitmap) {
    Glide.with(this)
        .load(bitmap)
        .into(this)
}

fun ImageView.loadQR(content: String, color: Int) {
    this.load(generateBitmap(maxOf(measuredWidth, measuredHeight, 720), color, content))
}

fun ImageView.loadWithFadeIn(@DrawableRes drawableRes: Int) {
    load(drawableRes)
    fadeIn()
}

fun ImageView.loadWithPlaceholder(url: String, @ColorRes colorRes: Int) {
    Glide.with(this)
        .load(url)
        .transition(withCrossFade(300))
        .placeholder(colorRes)
        .into(this)
}

fun ImageView.fadeIn() {
    alpha = 0f
    animate().alpha(1.0f).duration = 300
}
