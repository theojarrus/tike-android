package com.theost.tike.common.extension

import android.location.Address
import com.theost.tike.domain.model.core.Location

fun Address.mapToLocation(): Location {
    return Location(
        address = getAddressLine(maxAddressLineIndex),
        latitude = latitude,
        longitude = longitude
    )
}