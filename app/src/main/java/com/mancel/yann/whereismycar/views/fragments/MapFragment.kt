package com.mancel.yann.whereismycar.views.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.view.View
import androidx.annotation.RequiresPermission
import androidx.fragment.app.activityViewModels
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mancel.yann.whereismycar.R
import com.mancel.yann.whereismycar.WhereIsMyCarApplication
import com.mancel.yann.whereismycar.helpers.*
import com.mancel.yann.whereismycar.models.Location
import com.mancel.yann.whereismycar.models.POI
import com.mancel.yann.whereismycar.states.LocationState
import com.mancel.yann.whereismycar.states.WayState
import com.mancel.yann.whereismycar.viewModels.SharedViewModel
import com.mancel.yann.whereismycar.views.adapters.InfoWindowAdapter
import com.mancel.yann.whereismycar.views.adapters.OnClickInfoWindowListener
import kotlinx.android.synthetic.main.fragment_map.view.*
import java.util.*

/**
 * Created by Yann MANCEL on 08/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.views.activities
 *
 * A [BaseFragment] subclass which implements [OnMapReadyCallback],
 * [GoogleMap.OnCameraMoveStartedListener], [GoogleMap.OnMapLongClickListener],
 * [GoogleMap.OnMarkerClickListener], [GoogleMap.OnMarkerDragListener] and
 * [OnClickInfoWindowListener].
 */
class MapFragment : BaseFragment(), OnMapReadyCallback,
                                    GoogleMap.OnCameraMoveStartedListener,
                                    GoogleMap.OnMapLongClickListener,
                                    GoogleMap.OnMarkerClickListener,
                                    GoogleMap.OnMarkerDragListener,
                                    OnClickInfoWindowListener {

    // FIELDS --------------------------------------------------------------------------------------

    private val _viewModel: SharedViewModel by activityViewModels {
        (this.requireActivity().application as WhereIsMyCarApplication)._viewModelFactory
    }

    private lateinit var _map: GoogleMap
    private lateinit var _currentLocation: Location

    private var _isLocatedOnUser: Boolean = true
    private var _isFirstLocation: Boolean = true

    private var _checkedWayItem = 1

    private var _polyline: Polyline? = null

    // METHODS -------------------------------------------------------------------------------------

    // -- BaseFragment --

    override fun getFragmentLayout(): Int = R.layout.fragment_map

    override fun doOnCreateView() {
        this.configureActionOfFABs()
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
                    && grantResults.first() == PackageManager.PERMISSION_GRANTED
                ) {
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
                    longitudeOfCenter != this._currentLocation._longitude
                ) {
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

    // -- GoogleMap.OnMapLongClickListener interface --

    override fun onMapLongClick(pointOfMap: LatLng?) {
        if (!::_map.isInitialized || pointOfMap == null) return

        MaterialAlertDialogBuilder(this.requireContext())
            .setTitle(R.string.title_marker_dialog)
            .setSingleChoiceItems(R.array.marker_item, 0, null)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.accept) { _, _ ->
                this._viewModel.addPointOfInterest(
                    POI(_latitude = pointOfMap.latitude, _longitude = pointOfMap.longitude)
                )
            }
            .show()
    }

    // -- GoogleMap.OnMarkerClickListener interface --

    override fun onMarkerClick(marker: Marker?): Boolean = false

    // -- GoogleMap.OnMarkerDragListener interface --

    override fun onMarkerDragStart(marker: Marker?) { /* Do nothing here */ }

    override fun onMarkerDrag(marker: Marker?) { /* Do nothing here */ }

    override fun onMarkerDragEnd(marker: Marker?) {
        if (!::_map.isInitialized || marker == null) return

        this._viewModel.updatePointOfInterest(
            (marker.tag as POI)._id, marker.position.latitude, marker.position.longitude
        )
    }

    // -- OnClickInfoWindowListener interface --

    override fun onClickOnWayButton(marker: Marker) {
        MaterialAlertDialogBuilder(this.requireContext())
            .setTitle(R.string.title_way_dialog)
            .setSingleChoiceItems(R.array.way_item, this._checkedWayItem) { _, position ->
                this._checkedWayItem = position
            }
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.accept) { _, _ ->
                // Remove event of MapWrapperLayout (InfoWindow)
                this._rootView.fragment_map_wrapper.clearMarkerWithInfoWindow()

                // InfoWindow
                marker.hideInfoWindow()

                // Way to POI
                val poi = marker.tag as POI
                val singleItems = this.resources.getStringArray(R.array.way_item)
                this._viewModel.buildWay(
                    this._currentLocation.getLocationToGoogleMapsRequest(),
                    poi.getLocationToGoogleMapsRequest(),
                    singleItems[this._checkedWayItem].toLowerCase(Locale.getDefault())
                )
            }
            .show()
    }

    override fun onClickOnDeleteButton(marker: Marker) {
        MaterialAlertDialogBuilder(this.requireContext())
            .setTitle(R.string.title_delete_dialog)
            .setMessage(R.string.message_delete_dialog)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.accept) { _, _ ->
                // Remove event of MapWrapperLayout (InfoWindow)
                this._rootView.fragment_map_wrapper.clearMarkerWithInfoWindow()

                // InfoWindow
                marker.hideInfoWindow()

                // Delete POI
                val poi = marker.tag as POI
                this._viewModel.removePointOfInterest(poi)

                showMessageWithSnackbar(
                    this._rootView.fragment_map_root,
                    this.getString(R.string.message_delete_snackbar),
                    this.getString(R.string.cancel)
                ) {
                    val newPoi = poi.copy(_id = 0L)
                    this._viewModel.addPointOfInterest(newPoi)
                }
            }
            .show()
    }

    // -- Action --

    private fun configureActionOfFABs() {
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

        this._rootView.fragment_map_mini_fab.setOnClickListener {
            this._viewModel.clearWay()
            this._rootView.fragment_map_mini_fab.animateWithTranslationYAndEndAction(0F) {
                visibility = View.GONE
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

    @RequiresPermission(
        anyOf = [
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION"
        ]
    )
    private fun configureLocationEvents() {
        this._viewModel
            .getLocationState()
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

    private fun configureWayEvents() {
        this._viewModel
            .getWayState()
            .observe(this.viewLifecycleOwner) { wayState ->
                wayState?.let {
                    this.updateUIWithWayEvents(it)
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

    // -- Way events --

    private fun updateUIWithWayEvents(state: WayState) {
        when (state) {
            is WayState.Success -> this.handleWayStateWithSuccess(state)
            is WayState.Failure -> this.handleWayStateWithFailure(state)
            WayState.Clear -> this.handleWayStateWithClear()
        }
    }

    private fun handleWayStateWithSuccess(state: WayState.Success) {
        if (!::_map.isInitialized || state._way.isEmpty()) return

        // Polyline
        this._polyline = this._map.addPolyline(
            PolylineOptions()
                .addAll(getPolylineFromLocations(state._way))
                .color(Color.BLUE)
        )

        // Animation of FAB
        this._rootView.fragment_map_mini_fab.animateWithTranslationYAndStartAction(
            (-1.0F) * this.resources.getDimension(R.dimen.offset_y)
        ) {
            visibility = View.VISIBLE
        }
    }

    private fun handleWayStateWithFailure(state: WayState.Failure) {
        showMessageWithSnackbar(
            this._rootView.fragment_map_root,
            state._exception.message ?: this.getString(R.string.unknown_error)
        )
    }

    private fun handleWayStateWithClear() {
        if (!::_map.isInitialized || this._polyline == null) return
        this._polyline!!.remove()
        this._polyline = null
    }

    // -- Google Maps --

    @SuppressLint("MissingPermission")
    private fun enableUserLocation() {
        if (!::_map.isInitialized) return

        if (this.hasPermissionToAccessFineLocation()) {
            this.configureLocationEvents()
            this.configurePointsOfInterestEvents()
            this.configureWayEvents()
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
        this._map.setOnMapLongClickListener(this@MapFragment)

        // Marker
        this._map.setOnMarkerClickListener(this@MapFragment)
        this._map.setOnMarkerDragListener(this@MapFragment)

        // InfoWindow
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge
        this._rootView.fragment_map_wrapper.init(
            this._map,
            getPixelsFromDp(this.requireContext(), 39.0F + 20.0F)
        )
        this._map.setInfoWindowAdapter(
            InfoWindowAdapter(
                this.requireContext(),
                this._rootView.fragment_map_wrapper,
                this@MapFragment
            )
        )
    }

    // -- Point of interest --

    private fun updateUIWithPointsOfInterestEvents(pointsOfInterest: List<POI>) {
        if (!::_map.isInitialized) return

        // Remove Markers
        // todo - 29/10/2020 - If there is a way When you add POI, the way is removed.
        this._map.clear()

        // todo - 12/10/2020 - Put in lazy if just on type of POI
        val bitmap =
            try {
                getBitmapFromDrawableResource(this.requireContext(), R.drawable.ic_car_location)
            } catch (e: IllegalArgumentException) {
                null
            }

        pointsOfInterest.forEach { poi ->
            this._map.addMarker(
                MarkerOptions()
                    .position(LatLng(poi._latitude, poi._longitude))
                    .draggable(true)
                    .icon(
                        if (bitmap != null) BitmapDescriptorFactory.fromBitmap(bitmap)
                        else BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
            ).apply {
                // To identify what is the marker that is dragged by user
                tag = poi
            }
        }
    }
}