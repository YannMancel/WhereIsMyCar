package com.mancel.yann.whereismycar.views.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.fragment.app.activityViewModels
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.mancel.yann.whereismycar.R
import com.mancel.yann.whereismycar.helpers.*
import com.mancel.yann.whereismycar.models.Location
import com.mancel.yann.whereismycar.models.POI
import com.mancel.yann.whereismycar.states.LocationState
import com.mancel.yann.whereismycar.viewModels.SharedViewModel
import kotlinx.android.synthetic.main.fragment_map.view.*
import java.lang.IllegalArgumentException

/**
 * Created by Yann MANCEL on 08/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.views.activities
 *
 * A [BaseFragment] subclass which implements [OnMapReadyCallback],
 * [GoogleMap.OnCameraMoveStartedListener], [GoogleMap.OnMapClickListener],
 * [GoogleMap.OnMarkerClickListener] and [GoogleMap.OnMarkerDragListener].
 */
class MapFragment : BaseFragment(), OnMapReadyCallback,
                                    GoogleMap.OnCameraMoveStartedListener,
                                    GoogleMap.OnMapClickListener,
                                    GoogleMap.OnMarkerClickListener,
                                    GoogleMap.OnMarkerDragListener {

    // FIELDS --------------------------------------------------------------------------------------

    private val _viewModel: SharedViewModel by activityViewModels()

    private lateinit var _map: GoogleMap
    private lateinit var _currentLocation: Location

    private var _isLocatedOnUser: Boolean = true
    private var _isFirstLocation: Boolean = true

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

    // -- GoogleMap.OnMapClickListener interface --

    override fun onMapClick(pointOfMap: LatLng?) {
        if (!::_map.isInitialized || pointOfMap == null) return

        this._viewModel.addPointOfInterest(
            POI(_latitude = pointOfMap.latitude, _longitude = pointOfMap.longitude)
        )
    }

    // -- GoogleMap.OnMarkerClickListener interface --

    override fun onMarkerClick(marker: Marker?): Boolean = false

    // -- GoogleMap.OnMarkerDragListener interface --

    override fun onMarkerDragStart(marker: Marker?) { /* Do nothing here */ }

    override fun onMarkerDrag(marker: Marker?) { /* Do nothing here */ }

    override fun onMarkerDragEnd(marker: Marker?) {
        if (!::_map.isInitialized || marker == null) return

        this._viewModel.updatePointOfInterest(
            marker.tag as Long,
            marker.position.latitude, marker.position.longitude
        )
    }

    // -- Action --

    private fun configureActionOfFAB() {
        this._rootView.fragment_map_fab.setOnClickListener {
            if (!::_map.isInitialized || !::_currentLocation.isInitialized)
                return@setOnClickListener

            // Focusing on vision against the current position
            if (!this._isLocatedOnUser) {
                animateCameraOfGoogleMaps(this._map, this._currentLocation)

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

    @RequiresPermission(anyOf = [
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.ACCESS_FINE_LOCATION"
    ])
    private fun configureLocationEvents() {
        this._viewModel
            .getLocationState(this.requireContext())
            .observe(this.viewLifecycleOwner) { locationState ->
                locationState?.let {
                    this.updateUIWithLocationEvents(it)
                }
            }
    }

    private fun configurePointsOfInterestEvents() {
        this._viewModel
            .getPointsOfInterest()
            .observe(this.viewLifecycleOwner) { pointsOfInterest ->
                pointsOfInterest?.let {
                    this.updateUIWithPointsOfInterestEvents(it)
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
        if (!::_map.isInitialized) return

        // Location
        this._currentLocation = state._location

        // Focus on the current location of user
        if (this._isLocatedOnUser) {
            if (this._isFirstLocation) {
                moveCameraOfGoogleMaps(this._map, state._location)
                this._isFirstLocation = false
            } else {
                animateCameraOfGoogleMaps(this._map, state._location)
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
            this.configurePointsOfInterestEvents()
            configureStyleOfGoogleMaps(this._map, this@MapFragment)
            configureUiSettingsOfGoogleMaps(this._map)
            this.configureListenersOnGoogleMaps()
        } else {
            this.requestPermissionToAccessFineLocation()
        }
    }

    private fun configureListenersOnGoogleMaps() {
        if (!::_map.isInitialized) return

        this._map.setOnCameraMoveStartedListener(this@MapFragment)
        this._map.setOnMapClickListener(this@MapFragment)
        this._map.setOnMarkerClickListener(this@MapFragment)
        this._map.setOnMarkerDragListener(this@MapFragment)
    }

    // -- Point of interest --

    private fun updateUIWithPointsOfInterestEvents(pointsOfInterest: List<POI>) {
        if (!::_map.isInitialized) return

        // Remove Markers
        this._map.clear()

        // todo - 12/10/2020 - Put in lazy if just on type of POI
        val bitmap =
            try {
                getBitmapFromDrawableResource(this.requireContext(), R.drawable.ic_car)
            } catch (e: IllegalArgumentException) {
                return
            }

        pointsOfInterest.forEach { poi ->
            // MarkerOptions
            val marker =
                MarkerOptions()
                    .position(LatLng(poi._latitude, poi._longitude))
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))

            this._map.addMarker(marker).apply {
                // To identify what is the marker that is dragged by user
                tag = poi._id
            }
        }
    }
}