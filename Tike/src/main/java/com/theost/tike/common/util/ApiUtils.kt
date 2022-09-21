package com.theost.tike.common.util

object ApiUtils {

    fun getQualityAvatar(url: String): String {
        return url.split("=s").firstOrNull() ?: url
    }
}