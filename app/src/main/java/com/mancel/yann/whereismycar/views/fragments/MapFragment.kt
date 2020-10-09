package com.mancel.yann.whereismycar.views.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.fragment.app.activityViewModels
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.mancel.yann.whereismycar.R
import com.mancel.yann.whereismycar.helpers.*
import com.mancel.yann.whereismycar.states.LocationState
import com.mancel.yann.whereismycar.viewModels.SharedViewModel
import kotlinx.android.synthetic.main.fragment_map.view.*

/**
 * Created by Yann MANCEL on 08/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.views.activities
 *
 * A [BaseFragment] subclass.
 */
class MapFragment : BaseFragment(), OnMapReadyCallback {

    // FIELDS --------------------------------------------------------------------------------------

    private val _viewModel: SharedViewModel by activityViewModels()
    private lateinit var _map: GoogleMap

    // METHODS -------------------------------------------------------------------------------------

    // -- BaseFragment --

    override fun getFragmentLayout(): Int = R.layout.fragment_map

    override fun doOnCreateView() = this.configureMapFragmentOfGoogleMaps()

    // -- Fragment --

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            // Access to the current location
            REQUEST_CODE_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty()
                    && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    this.enableUserLocation()

                    // When user cancels location permission during the runtime
                    this._viewModel.requestUpdateLocationAfterPermission()
                } else {
                    showMessageWithSnackbar(
                        this._rootView.fragment_map_root,
                        this.getString(R.string.no_permission)
                    )
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check settings to location
        if (requestCode == REQUEST_CODE_CHECK_SETTINGS_TO_LOCATION
            && resultCode == Activity.RESULT_OK) {
            this._viewModel.requestUpdateLocationAfterPermission()
        }
    }

    // -- OnMapReadyCallback interface --

    override fun onMapReady(googleMap: GoogleMap?) {
        this._map = googleMap ?: return
        this.enableUserLocation()
    }

    // -- MapFragment --

    private fun configureMapFragmentOfGoogleMaps() {
        var childFragment =
            this.childFragmentManager
                .findFragmentById(R.id.fragment_map_support_map) as? SupportMapFragment

        if (childFragment == null) {
            childFragment = SupportMapFragment.newInstance()

            this.childFragmentManager.beginTransaction()
                .add(R.id.fragment_map_support_map, childFragment)
                .commit()
        }

        childFragment?.getMapAsync(this@MapFragment)
    }

    // -- LiveData --

    private fun configureLocationEvents() {
        this._viewModel
            .getLocationState(this.requireContext())
            .observe(this.viewLifecycleOwner) { locationState ->
                locationState?.let {
                    this.updateUIWithLocationEvents(it)
                }
            }
    }

    // -- Location events --

    private fun updateUIWithLocationEvents(state: LocationState) {
        when (state) {
            is LocationState.Success -> this.handleLocationStateWithSuccess(state)
            is LocationState.Failure -> this.handleLocationStateWithFailure(state)
        }
    }

    private fun handleLocationStateWithSuccess(state: LocationState.Success) {
        Log.d("LOCATION", "latitude: ${state._location._latitude} - longitude: ${state._location._longitude}")
    }

    private fun handleLocationStateWithFailure(state: LocationState.Failure) {
        when (val exception = state._exception) {
            is SecurityException -> this.requestAccessFineLocationPermission()

            is ResolvableApiException -> {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this.requireActivity(),
                        REQUEST_CODE_CHECK_SETTINGS_TO_LOCATION
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }

            else -> {
                showMessageWithSnackbar(
                    this._rootView.fragment_map_root,
                    exception.message ?: this.getString(R.string.unknown_error)
                )
            }
        }
    }

    // -- Google Maps --

    @SuppressLint("MissingPermission")
    private fun enableUserLocation() {
        if (!::_map.isInitialized) return

        if (this.hasAccessFineLocationPermission()) {
            this.configureLocationEvents()
            this.configureGoogleMapStyle()

            // Add a marker in Sydney and move the camera
            val sydney = LatLng(-34.0, 151.0)
            this._map.addMarker(
                MarkerOptions()
                    .position(sydney)
                    .title("Marker in Sydney")
            )
            this._map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        } else {
            this.requestAccessFineLocationPermission()
        }
    }

    @RequiresPermission(anyOf = [
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.ACCESS_FINE_LOCATION"
    ])
    private fun configureGoogleMapStyle() {
        if (!::_map.isInitialized) return

        with(this._map) {
            // STYLE
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                val isSuccess: Boolean =
                    setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            this@MapFragment.requireContext(),
                            R.raw.style_map_night
                        )
                    )

                if (!isSuccess) {
                    Log.e(this@MapFragment.javaClass.simpleName, "Style parsing failed.")
                }
            } catch (e: Resources.NotFoundException) {
                Log.e(this@MapFragment.javaClass.simpleName, "Can't find style. Error: ", e)
            }

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

            // MY LOCATION
            isMyLocationEnabled = true
            uiSettings?.isMyLocationButtonEnabled = false

            // TOOLBAR
            uiSettings?.isMapToolbarEnabled = false
        }
    }
}