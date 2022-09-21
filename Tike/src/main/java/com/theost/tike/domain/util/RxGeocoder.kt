package com.theost.tike.domain.util

import android.location.Address
import android.location.Geocoder
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import io.reactivex.Single

object RxGeocoder {

    fun getFromLocation(
        geocoder: Geocoder,
        lat: Double,
        lng: Double,
        maxResults: Int
    ): Single<List<Address>> {
        return Single.create { emitter ->
            if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(lat, lng, maxResults) { emitter.onSuccess(it) }
            } else {
                @Suppress("DEPRECATION")
                emitter.onSuccess(geocoder.getFromLocation(lat, lng, maxResults).orEmpty())
            }
        }
    }

    fun getFromLocationName(
        geocoder: Geocoder,
        locationName: String,
        maxResults: Int
    ): Single<List<Address>> {
        return Single.create { emitter ->
            if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(locationName, maxResults) { emitter.onSuccess(it) }
            } else {
                @Suppress("DEPRECATION")
                emitter.onSuccess(geocoder.getFromLocationName(locationName, maxResults).orEmpty())
            }
        }
    }
}
