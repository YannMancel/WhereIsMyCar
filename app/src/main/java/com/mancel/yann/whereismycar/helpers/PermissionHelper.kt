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
object PermissionHelper {

    // FIELDS --------------------------------------------------------------------------------------

    const val REQUEST_CODE_CHECK_SETTINGS_TO_LOCATION = 1000
    const val REQUEST_CODE_ACCESS_FINE_LOCATION = 2000
    const val REQUEST_CODE_ACCESS_COARSE_LOCATION = 3000

    private const val PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private const val PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION

    // METHODS -------------------------------------------------------------------------------------

    /** Checks the permission: [Manifest.permission.ACCESS_FINE_LOCATION] */
    fun hasAccessFineLocationPermission(fragment: Fragment): Boolean {
        val permissionResult = ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            PERMISSION_ACCESS_FINE_LOCATION
        )
        return permissionResult == PackageManager.PERMISSION_GRANTED
    }

    /** Requests the permission: [Manifest.permission.ACCESS_FINE_LOCATION] */
    fun requestAccessFineLocationPermission(fragment: Fragment) {
        fragment.requestPermissions(
            arrayOf(PERMISSION_ACCESS_FINE_LOCATION),
            REQUEST_CODE_ACCESS_FINE_LOCATION
        )
    }
}