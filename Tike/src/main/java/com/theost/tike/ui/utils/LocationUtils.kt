package com.theost.tike.ui.utils

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager

object LocationUtils {

    const val MOSCOW_LAT = 55.7558
    const val MOSCOW_LNG = 37.6173

    fun hasLocationPermission(context: Context): Boolean {
        return context.checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
    }

    fun isLocationEnabled(context: Context): Boolean {
        return (context.getSystemService(LOCATION_SERVICE) as LocationManager).isLocationEnabled
    }
}