package com.theost.tike.ui.extensions

import android.location.Address
import com.theost.tike.data.models.core.Location

fun Address.mapToLocation(): Location {
    return Location(
        address = getAddressLine(maxAddressLineIndex),
        latitude = latitude,
        longitude = longitude
    )
}