package com.theost.tike.network.widget

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest.Builder
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.theost.tike.common.extension.isConnected
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.network.model.multi.ConnectionStatus
import com.theost.tike.network.model.multi.ConnectionStatus.Connected
import com.theost.tike.network.model.multi.ConnectionStatus.Disconnected

class ConnectionTracker(private val connectivityManager: ConnectivityManager) : NetworkCallback() {

    private val _status = MutableLiveData<ConnectionStatus>()
    val status: LiveData<ConnectionStatus> = _status

    private val defaultRequest = Builder().build()
    private var isRegistered = false

    init {
        val initialStatus = if (connectivityManager.isConnected()) Connected else Disconnected
        updateStatus(initialStatus)
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        updateStatus(Connected)
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        updateStatus(Disconnected)
    }

    private fun updateStatus(networkStatus: ConnectionStatus) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            _status.value = networkStatus
        } else {
            _status.postValue(networkStatus)
        }
    }

    fun start() {
        if (!isRegistered) {
            runCatching {
                connectivityManager.registerNetworkCallback(defaultRequest, this)
                isRegistered = true
            }.onFailure { error ->
                log(this, error)
            }
        }
    }

    fun stop() {
        if (isRegistered) {
            runCatching {
                connectivityManager.unregisterNetworkCallback(this)
                isRegistered = false
            }.onFailure { error ->
                log(this, error)
            }
        }
    }
}
