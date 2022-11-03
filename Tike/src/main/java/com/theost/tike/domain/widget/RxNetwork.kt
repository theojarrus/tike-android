package com.theost.tike.domain.widget

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import io.reactivex.Observable

class RxNetwork(private val manager: ConnectivityManager) {

    fun observe(): Observable<Boolean> {
        var networkCallback: NetworkCallback? = null
        val networkRequest = NetworkRequest.Builder().build()
        return Observable.create<Boolean?> { emitter ->
            networkCallback = object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    emitter.onNext(true)
                }
                override fun onLost(network: Network) {
                    super.onLost(network)
                    emitter.onNext(false)
                }
            }
            networkCallback?.let { manager.registerNetworkCallback(networkRequest, it) }
        }.doOnDispose { networkCallback?.let { manager.unregisterNetworkCallback(it) } }
    }
}
