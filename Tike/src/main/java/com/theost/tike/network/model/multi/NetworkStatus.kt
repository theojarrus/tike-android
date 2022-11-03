package com.theost.tike.network.model.multi

sealed class NetworkStatus {

    object Online : NetworkStatus()
    object Offline : NetworkStatus()
    object Unsupported : NetworkStatus()
}
