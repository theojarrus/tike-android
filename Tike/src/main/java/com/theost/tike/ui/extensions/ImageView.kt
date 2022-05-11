package com.theost.tike.ui.extensions

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.theost.tike.ui.widgets.QrCodeGenerator.generateBitmap

fun ImageView.load(@DrawableRes drawableRes: Int) {
    Glide.with(this)
        .load(drawableRes)
        .into(this)
}

fun ImageView.load(url: String) {
    Glide.with(this)
        .load(url)
        .into(this)
}

fun ImageView.load(bitmap: Bitmap) {
    Glide.with(this)
        .load(bitmap)
        .into(this)
}

fun ImageView.load(url: String, @ColorRes placeholder: Int, @ColorRes error: Int) {
    Glide.with(this)
        .load(url)
        .placeholder(placeholder)
        .error(error)
        .into(this)
}

fun ImageView.loadQR(content: String, color: Int) {
    this.load(generateBitmap(maxOf(measuredWidth, measuredHeight, 720), color, content))
}