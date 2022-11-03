package com.theost.tike.domain.model.core.mapper

import com.theost.tike.domain.model.core.Location

class LocationMapper : (String?, Double?, Double?) -> Location? {

    override fun invoke(address: String?, latitude: Double?, longitude: Double?): Location? {
        return if (address != null && latitude != null && longitude != null) {
            Location(address, latitude, longitude)
        } else {
            null
        }
    }
}
