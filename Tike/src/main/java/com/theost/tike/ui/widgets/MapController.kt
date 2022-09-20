package com.theost.tike.ui.widgets

import android.content.Context
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.theost.tike.R
import com.theost.tike.data.models.core.Location
import com.theost.tike.ui.extensions.fazy
import com.theost.tike.ui.utils.DisplayUtils.showError
import com.theost.tike.ui.utils.LocationUtils.MOSCOW_LAT
import com.theost.tike.ui.utils.LocationUtils.MOSCOW_LNG
import com.theost.tike.ui.utils.LocationUtils.isLocationEnabled

class MapController(
    val context: Context,
    fragmentManager: FragmentManager,
    @IdRes id: Int
) {

    private val mapFragment by fazy { fragmentManager.findFragmentById(id) as SupportMapFragment }

    fun init(location: Location?, isEditable: Boolean) {
        mapFragment.getMapAsync { map ->
            map.apply {
                location?.also { updateLocation(it.latitude, it.longitude) } ?: initCamera(map)
                if (isEditable) {
                    setOnMapClickListener { location ->
                        mapListener?.let { it(location.latitude, location.longitude) }
                        updateLocation(location.latitude, location.longitude, true)
                    }
                }
                setOnMyLocationButtonClickListener {
                    if (!isLocationEnabled(context)) showError(context, R.string.error_location)
                    false
                }
            }
        }
    }

    fun initCamera(map: GoogleMap) {
        map.moveCamera(newLatLngZoom(LatLng(MOSCOW_LAT, MOSCOW_LNG), DEFAULT_ZOOM))
    }

    fun updateLocation(latitude: Double, longitude: Double, isSmooth: Boolean = false) {
        mapFragment.getMapAsync { map ->
            LatLng(latitude, longitude).let { location ->
                map.apply {
                    clear()
                    addMarker(MarkerOptions().position(location))
                    newLatLngZoom(location, ADDRESS_ZOOM).let { update ->
                        if (isSmooth) animateCamera(update) else moveCamera(update)
                    }
                }
            }
        }
    }

    fun clearLocation() {
        mapFragment.getMapAsync { it.clear() }
    }

    fun enableMyLocation() {
        mapFragment.getMapAsync { it.isMyLocationEnabled = true }
    }

    private var mapListener: ((Double, Double) -> Unit)? = null

    fun setMapListener(listener: ((Double, Double) -> Unit)?) {
        mapListener = listener
    }

    companion object {

        private const val DEFAULT_ZOOM = 10f
        private const val ADDRESS_ZOOM = 17f
    }
}