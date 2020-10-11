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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.mancel.yann.whereismycar.R
import com.mancel.yann.whereismycar.helpers.*
import com.mancel.yann.whereismycar.models.Location
import com.mancel.yann.whereismycar.states.LocationState
import com.mancel.yann.whereismycar.viewModels.SharedViewModel
import kotlinx.android.synthetic.main.fragment_map.view.*

/**
 * Created by Yann MANCEL on 08/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.views.activities
 *
 * A [BaseFragment] subclass which implements [OnMapReadyCallback] and
 * [GoogleMap.OnCameraMoveStartedListener].
 */
class MapFragment : BaseFragment(), OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener {

    // ENUMS ---------------------------------------------------------------------------------------

    enum class MapStyle {STANDARD, SILVER, RETRO, DARK, NIGHT, AUBERGINE}

    // FIELDS --------------------------------------------------------------------------------------

    private val _viewModel: SharedViewModel by activityViewModels()

    private lateinit var _map: GoogleMap

    private lateinit var _currentLocation: Location

    private var _isLocatedOnUser: Boolean = true
    private var _isFirstLocation: Boolean = true

    companion object {
        const val DEFAULT_ZOOM = 17F
    }

    // METHODS -------------------------------------------------------------------------------------

    // -- BaseFragment --

    override fun getFragmentLayout(): Int = R.layout.fragment_map

    override fun doOnCreateView() {
        this.configureActionOfFAB()
        this.configureMapFragmentOfGoogleMaps()
    }

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

    // -- GoogleMap.OnCameraMoveStartedListener interface --

    override fun onCameraMoveStarted(reason: Int) {
        when (reason) {
            // The user gestured on the map (ex: Zoom or Rotation)
            GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE -> {
                if (!::_currentLocation.isInitialized) return

                val latitudeOfCenter =
                    this._map.projection?.visibleRegion?.latLngBounds?.center?.latitude

                val longitudeOfCenter =
                    this._map.projection?.visibleRegion?.latLngBounds?.center?.longitude

                // Projection's Center (visible region) = current location of user
                // (ex: zoom or rotation)
                if (latitudeOfCenter != this._currentLocation._latitude &&
                    longitudeOfCenter != this._currentLocation._longitude) {
                    this._isLocatedOnUser = false
                }
            }

            // The user tapped something on the map (ex: tap on marker)
            GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION -> {
                this._isLocatedOnUser = false
            }

            // The app moved the camera (ex: GoogleMap#moveCamera of GoogleMap#animateCamera)
            GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION -> {
                /* Ignore this reason */
            }
        }
    }

    // -- Action --

    private fun configureActionOfFAB() {
        this._rootView.fragment_map_fab.setOnClickListener {
            if (!::_currentLocation.isInitialized) return@setOnClickListener

            // Focusing on vision against the current position
            if (!this._isLocatedOnUser) {
                this.animateCameraOfGoogleMaps(this._currentLocation)

                // Reset: Camera focus on user
                this._isLocatedOnUser = true
            }
        }
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
        // Location
        this._currentLocation = state._location

        // Focus on the current location of user
        if (this._isLocatedOnUser) {
            if (this._isFirstLocation) {
                this.moveCameraOfGoogleMaps(state._location)
                this._isFirstLocation = false
            } else {
                this.animateCameraOfGoogleMaps(state._location)
            }
        }
    }

    private fun handleLocationStateWithFailure(state: LocationState.Failure) {
        when (val exception = state._exception) {
            is SecurityException -> this.requestPermissionToAccessFineLocation()

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

        if (this.hasPermissionToAccessFineLocation()) {
            this.configureLocationEvents()
            this.configureStyleOfGoogleMaps()
            this.configureUiSettingsOfGoogleMaps()

            // Listener Camera
            this._map.setOnCameraMoveStartedListener(this@MapFragment)
        } else {
            this.requestPermissionToAccessFineLocation()
        }
    }

    private fun configureStyleOfGoogleMaps(style: MapStyle = MapStyle.STANDARD) {
        if (!::_map.isInitialized) return

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
                this._map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this@MapFragment.requireContext(),
                        rawValue
                    )
                )

            if (!isSuccess) {
                Log.e(this@MapFragment.javaClass.simpleName, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(this@MapFragment.javaClass.simpleName, "Can't find style. Error: ", e)
        }
    }

    @RequiresPermission(anyOf = [
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.ACCESS_FINE_LOCATION"
    ])
    private fun configureUiSettingsOfGoogleMaps() {
        if (!::_map.isInitialized) return

        with(this._map) {
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

    private fun moveCameraOfGoogleMaps(location: Location) {
        if (!::_map.isInitialized) return

        // Location
        val target = LatLng(location._latitude, location._longitude)

        // Camera
        this._map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, DEFAULT_ZOOM))
    }

    private fun animateCameraOfGoogleMaps(location: Location) {
        if (!::_map.isInitialized) return

        // Location
        val target = LatLng(location._latitude, location._longitude)

        // CameraPosition
        val cameraPosition = CameraPosition.Builder()
            .target(target)
            .zoom(DEFAULT_ZOOM)
            .build()

        // Camera
        this._map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }
}