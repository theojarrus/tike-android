package com.theost.tike.network.widget

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.theost.tike.common.util.LogUtils.log
import com.theost.tike.domain.repository.*
import com.theost.tike.network.business.ManageNetworkInteractor
import com.theost.tike.network.business.ManageRepositoriesInteractor
import com.theost.tike.network.model.multi.ConnectionStatus
import com.theost.tike.network.model.multi.NetworkStatus
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class NetworkManager(private val networkTracker: ConnectionTracker) {

    private val _status = MutableLiveData<NetworkStatus>()
    val status: LiveData<NetworkStatus> = _status

    private lateinit var networkSubject: PublishSubject<ConnectionStatus>
    private val networkObserver = Observer<ConnectionStatus> { networkSubject.onNext(it) }

    private val compositeDisposable = CompositeDisposable()
    private var isWorking = false

    private val manageRepositoriesInteractor = ManageRepositoriesInteractor(
        AuthRepository,
        EventsRepository,
        FriendsRepository,
        UsersRepository
    )

    private val manageNetworkInteractor = ManageNetworkInteractor(
        ConfigRepository,
        StoreRepository,
        manageRepositoriesInteractor
    )

    private fun create() {
        networkSubject = PublishSubject.create()
    }

    private fun observe() {
        networkTracker.status.observeForever(networkObserver)
    }

    private fun manage() {
        compositeDisposable.add(
            networkSubject
                .distinctUntilChanged()
                .flatMapSingle { connectionStatus ->
                    manageNetworkInteractor(connectionStatus)
                }.subscribe({ appStatus ->
                    updateStatus(appStatus)
                }, { error ->
                    stop()
                    log(this, error)
                })
        )
    }

    fun clear() {
        networkTracker.status.removeObserver(networkObserver)
        compositeDisposable.clear()
    }

    private fun updateStatus(appStatus: NetworkStatus) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            _status.value = appStatus
        } else {
            _status.postValue(appStatus)
        }
    }

    fun start() {
        if (!isWorking) {
            create()
            manage()
            observe()
            isWorking = true
        }
    }

    fun stop() {
        if (isWorking) {
            clear()
            isWorking = false
        }
    }
}
