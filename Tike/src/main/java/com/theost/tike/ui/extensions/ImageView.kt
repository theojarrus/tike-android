package com.theost.tike.ui.extensions

import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide

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

fun ImageView.load(url: String, @ColorRes placeholder: Int, @ColorRes error: Int) {
    Glide.with(this)
        .load(url)
        .placeholder(placeholder)
        .error(error)
        .into(this)
}