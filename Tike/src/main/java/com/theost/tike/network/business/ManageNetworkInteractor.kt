package com.theost.tike.network.business

import com.theost.tike.domain.config.Config.toggle
import com.theost.tike.domain.config.Toggle.APP_VERSION_SUPPORTED
import com.theost.tike.domain.repository.ConfigRepository
import com.theost.tike.domain.repository.StoreRepository
import com.theost.tike.network.model.multi.ConnectionStatus
import com.theost.tike.network.model.multi.ConnectionStatus.Connected
import com.theost.tike.network.model.multi.ConnectionStatus.Disconnected
import com.theost.tike.network.model.multi.NetworkStatus
import com.theost.tike.network.model.multi.NetworkStatus.*
import io.reactivex.Completable
import io.reactivex.Single

class ManageNetworkInteractor(
    private val configRepository: ConfigRepository,
    private val storeRepository: StoreRepository,
    private val manageRepositoriesInteractor: ManageRepositoriesInteractor
) {

    operator fun invoke(connectionStatus: ConnectionStatus): Single<NetworkStatus> {
        return storeRepository.disableRemoteStore()
            .andThen(configRepository.fetchConfig().onErrorReturn { false })
            .flatMap {
                toggle(APP_VERSION_SUPPORTED).let { isSupported ->
                    when {
                        isSupported && connectionStatus is Disconnected -> {
                            storeRepository.disableRemoteStore().manageStatus(Offline)
                        }
                        isSupported && connectionStatus is Connected -> {
                            storeRepository.enableRemoteStore().manageStatus(Online)
                        }
                        else -> {
                            storeRepository.disableRemoteStore().manageStatus(Unsupported)
                        }
                    }
                }
            }
    }

    private fun Completable.manageStatus(networkStatus: NetworkStatus): Single<NetworkStatus> {
        return onErrorComplete()
            .andThen(manageRepositoriesInteractor(networkStatus))
            .andThen(Single.just(networkStatus))
    }
}
