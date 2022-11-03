package com.theost.tike.common.extension

import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI

fun ConnectivityManager.isConnected(): Boolean {
    with(getNetworkCapabilities(activeNetwork)) {
        return this != null && (hasTransport(TRANSPORT_CELLULAR) || hasTransport(TRANSPORT_WIFI))
    }
}
