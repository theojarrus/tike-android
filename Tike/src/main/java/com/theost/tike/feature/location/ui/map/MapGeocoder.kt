package com.theost.tike.feature.location.ui.map

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.theost.tike.common.extension.Locale.RU
import com.theost.tike.common.extension.mapToLocation
import com.theost.tike.domain.model.core.Location
import com.theost.tike.domain.widget.RxGeocoder
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.newThread

class MapGeocoder(val context: Context) {

    private val geocoder = Geocoder(context, RU)

    fun getLocation(latitude: Double, longitude: Double): Single<Location> {
        return RxGeocoder.getFromLocation(geocoder, latitude, longitude, MAX_RESULTS_FROM_LOCATION)
            .map { addresses ->
                addresses.run {
                    if (isNotEmpty()) {
                        get(0).mapToLocation()
                    } else {
                        Location("$latitude, $longitude", latitude, longitude)
                    }
                }
            }
            .subscribeOn(newThread())
    }

    fun getLocations(address: String): Single<List<Location>> {
        return RxGeocoder.getFromLocationName(geocoder, address, MAX_RESULTS_FROM_NAME)
            .map { it.map(Address::mapToLocation) }
            .subscribeOn(newThread())
    }

    companion object {

        private const val MAX_RESULTS_FROM_LOCATION = 1
        private const val MAX_RESULTS_FROM_NAME = 1
    }
}
