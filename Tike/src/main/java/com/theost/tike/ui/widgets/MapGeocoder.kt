package com.theost.tike.ui.widgets

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.theost.tike.data.models.core.Location
import com.theost.tike.ui.extensions.mapToLocation
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*

class MapGeocoder(val context: Context) {

    private val geocoder = Geocoder(context, Locale("ru"))

    fun getLocation(latitude: Double, longitude: Double): Single<Location> {
        return Single.just(Pair(latitude, longitude)).map { location ->
            val lat = location.first
            val long = location.second
            geocoder.getFromLocation(lat, long, 1).run {
                if (isEmpty()) {
                    Location("$lat, $long", lat, long)
                } else {
                    get(0).mapToLocation()
                }
            }
        }.subscribeOn(Schedulers.newThread())
    }

    fun getLocations(address: String): List<Location> {
        return geocoder.getFromLocationName(address, 15).map(Address::mapToLocation)
    }
}