package com.mancel.yann.whereismycar.helpers

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Created by Yann MANCEL on 08/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.helpers
 */

// FIELDS ------------------------------------------------------------------------------------------

const val REQUEST_CODE_CHECK_SETTINGS_TO_LOCATION = 1000
const val REQUEST_CODE_ACCESS_FINE_LOCATION = 2000

private const val PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION

// METHODS -----------------------------------------------------------------------------------------

fun Fragment.hasAccessFineLocationPermission(): Boolean {
    val permissionResult = ContextCompat.checkSelfPermission(
        this.requireContext(),
        PERMISSION_ACCESS_FINE_LOCATION
    )
    return permissionResult == PackageManager.PERMISSION_GRANTED
}

fun Fragment.requestAccessFineLocationPermission() {
    this.requestPermissions(
        arrayOf(PERMISSION_ACCESS_FINE_LOCATION),
        REQUEST_CODE_ACCESS_FINE_LOCATION
    )
}