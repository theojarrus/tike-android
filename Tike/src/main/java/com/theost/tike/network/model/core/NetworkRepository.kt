package com.theost.tike.network.model.core

import com.theost.tike.network.model.multi.NetworkStatus
import com.theost.tike.network.model.multi.NetworkStatus.Online
import io.reactivex.*

abstract class NetworkRepository {

    private var networkStatus: NetworkStatus? = null

    private val networkCompletable: Completable
        get() = when (networkStatus) {
            Online -> Completable.complete()
            else -> Completable.error(Exception())
        }

    fun onNetworkChange(networkStatus: NetworkStatus) {
        this.networkStatus = networkStatus
    }

    protected fun requireNetwork(block: () -> CompletableSource): Completable {
        return networkCompletable.andThen(block())
    }

    protected fun <T> requireNetwork(block: () -> SingleSource<T>): Single<T> {
        return networkCompletable.andThen(block())
    }

    protected fun <T> requireNetwork(block: () -> ObservableSource<T>): Observable<T> {
        return networkCompletable.andThen(block())
    }
}
