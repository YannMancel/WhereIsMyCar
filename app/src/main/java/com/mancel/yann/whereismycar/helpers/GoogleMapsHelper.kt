package com.mancel.yann.whereismycar.helpers

import android.content.res.Resources
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.mancel.yann.whereismycar.R
import com.mancel.yann.whereismycar.models.Location

/**
 * Created by Yann MANCEL on 13/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.helpers
 */

// ENUMS -------------------------------------------------------------------------------------------

enum class MapStyle {STANDARD, SILVER, RETRO, DARK, NIGHT, AUBERGINE}

// FIELDS ------------------------------------------------------------------------------------------

const val DEFAULT_ZOOM = 17F

// METHODS -----------------------------------------------------------------------------------------

fun configureStyleOfGoogleMaps(
    map: GoogleMap,
    fragment: Fragment,
    style: MapStyle = MapStyle.STANDARD
) {
    val rawValue = when (style) {
        MapStyle.STANDARD -> R.raw.style_map_standard
        MapStyle.SILVER -> R.raw.style_map_silver
        MapStyle.RETRO -> R.raw.style_map_retro
        MapStyle.DARK -> R.raw.style_map_dark
        MapStyle.NIGHT -> R.raw.style_map_night
        MapStyle.AUBERGINE -> R.raw.style_map_aubergine
    }

    // STYLE
    try {
        // Customise the styling of the base map using a JSON object defined
        // in a raw resource file.
        val isSuccess: Boolean =
            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    fragment.requireContext(),
                    rawValue
                )
            )

        if (!isSuccess) {
            Log.e(fragment.javaClass.simpleName, "Style parsing failed.")
        }
    } catch (e: Resources.NotFoundException) {
        Log.e(fragment.javaClass.simpleName, "Can't find style. Error: ", e)
    }
}

@RequiresPermission(anyOf = [
    "android.permission.ACCESS_COARSE_LOCATION",
    "android.permission.ACCESS_FINE_LOCATION"
])
fun configureUiSettingsOfGoogleMaps(map: GoogleMap) {
    with(map) {
        // GESTURES
        uiSettings?.isZoomGesturesEnabled = true
        uiSettings?.isRotateGesturesEnabled = true

        // SCROLL
        uiSettings?.isScrollGesturesEnabled = true
        uiSettings?.isScrollGesturesEnabledDuringRotateOrZoom = true

        // MIN ZOOM LEVELS
        setMinZoomPreference(if (minZoomLevel > 10.0F) minZoomLevel else 10.0F)

        // MAX ZOOM LEVELS
        setMaxZoomPreference(if (maxZoomLevel < 21.0F) maxZoomLevel else 21.0F)

        // MY LOCATION (Require permission)
        isMyLocationEnabled = true
        uiSettings?.isMyLocationButtonEnabled = false

        // TOOLBAR
        uiSettings?.isMapToolbarEnabled = false
    }
}

fun moveCameraOfGoogleMaps(map: GoogleMap, location: Location) {
    // Location
    val target = LatLng(location._latitude, location._longitude)

    // Camera
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, DEFAULT_ZOOM))
}

fun animateCameraOfGoogleMaps(map: GoogleMap, location: Location) {
    // Location
    val target = LatLng(location._latitude, location._longitude)

    // CameraPosition
    val cameraPosition = CameraPosition.Builder()
        .target(target)
        .zoom(DEFAULT_ZOOM)
        .build()

    // Camera
    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
}