package com.theost.tike.ui.utils

object ApiUtils {

    fun getQualityAvatar(url: String): String {
        return url.split("=s").firstOrNull() ?: url
    }
}