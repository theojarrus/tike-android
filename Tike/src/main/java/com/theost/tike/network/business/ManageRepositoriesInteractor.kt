package com.theost.tike.network.business

import com.theost.tike.network.model.core.NetworkRepository
import com.theost.tike.network.model.multi.NetworkStatus
import io.reactivex.Completable

class ManageRepositoriesInteractor(private vararg val repositories: NetworkRepository) {

    operator fun invoke(networkStatus: NetworkStatus): Completable {
        return Completable.fromAction {
            repositories.forEach { it.onNetworkChange(networkStatus) }
        }
    }
}
