package com.theost.tike.network.model.multi

sealed class ConnectionStatus {

    object Connected : ConnectionStatus()
    object Disconnected : ConnectionStatus()
}
