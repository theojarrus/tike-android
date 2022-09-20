package com.theost.tike.data.models.core

import java.io.Serializable

data class Location(
    val address: String,
    val latitude: Double,
    val longitude: Double
) : Serializable