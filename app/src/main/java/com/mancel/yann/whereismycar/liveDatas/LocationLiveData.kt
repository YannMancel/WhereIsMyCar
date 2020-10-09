package com.mancel.yann.whereismycar.liveDatas

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*
import com.mancel.yann.whereismycar.models.Location
import com.mancel.yann.whereismycar.R
import com.mancel.yann.whereismycar.states.LocationState
import kotlin.Exception

/**
 * Created by Yann MANCEL on 08/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar
 *
 * A [LiveData] of [LocationState] subclass.
 */
class LocationLiveData(context: Context) : LiveData<LocationState>() {

    // FIELDS --------------------------------------------------------------------------------------

    private val _context = context.applicationContext

    private lateinit var _client: FusedLocationProviderClient
    private lateinit var _request: LocationRequest
    private lateinit var _callback: LocationCallback

    private var _isFirstSubscriber: Boolean = true

    // CONSTRUCTORS --------------------------------------------------------------------------------

    init {
        this.configureFusedLocationProviderClient()
        this.configureLocationRequest()
        this.configureLocationCallback()
    }

    // METHODS -------------------------------------------------------------------------------------

    // -- LiveData --

    override fun onActive() {
        super.onActive()

        if (this._isFirstSubscriber) {
            this.requestLastLocation()
            this.requestUpdateLocation()

            // For the other subscribers
            this._isFirstSubscriber = false
        }
    }

    override fun onInactive() {
        super.onInactive()

        this._client.removeLocationUpdates(this._callback)

        // For the other subscribers
        this._isFirstSubscriber = true
    }

    // -- Google Maps --

    private fun configureFusedLocationProviderClient() {
        this._client = LocationServices.getFusedLocationProviderClient(this._context)
    }

    private fun configureLocationRequest() {
        this._request = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun configureLocationCallback() {
        this._callback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {
                // No result
                locationResult ?: return

                for (location in locationResult.locations) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    // Error: Out of boundaries of coordinates
                    if (!(latitude >= -90.0 && latitude <= 90.0 && longitude >= -180.0 && longitude <= 180.0)) {
                        return
                    }

                    // Not useful: Same LocationData
                    this@LocationLiveData.value?.let { locationState ->
                        if (locationState is LocationState.Success
                            && locationState._location._latitude == latitude
                            && locationState._location._longitude == longitude) return
                    }

                    this@LocationLiveData.changeLocationStateToSuccess(
                        Location(latitude, longitude)
                    )
                }
            }

            override fun onLocationAvailability(availability: LocationAvailability?) {
                // No data
                availability ?: return

                // No availability
                if (!availability.isLocationAvailable) {
                    this@LocationLiveData.changeLocationStateToFailure(
                        Exception(
                            this@LocationLiveData._context.getString(R.string.location_no_available)
                        )
                    )
                }
            }
        }
    }

    // -- LocationState --

    private fun changeLocationStateToSuccess(location: Location) {
        this.value = LocationState.Success(location)
    }

    private fun changeLocationStateToFailure(exception: Exception) {
        this.value = LocationState.Failure(exception)
    }

    // -- Location --

    @SuppressLint("MissingPermission")
    private fun requestLastLocation() {
        // TASK: Task<android.location.Location>
        this._client
            .lastLocation
            .addOnSuccessListener { location ->
                // Got last known location. In some rare situations this can be null.
                location?.let {
                    this.changeLocationStateToSuccess(Location(it.latitude, it.longitude))
                }
            }
            .addOnFailureListener { exception ->
                this.changeLocationStateToFailure(exception)
            }
    }

    fun requestUpdateLocation() {
        val builder =
            LocationSettingsRequest
                .Builder()
                .addLocationRequest(this._request)

        val client = LocationServices.getSettingsClient(this._context)

        // TASK: Task<LocationSettingsResponse>
        client
            .checkLocationSettings(builder.build())
            .addOnSuccessListener {
                this.requestLocation()
            }
            .addOnFailureListener { exception ->
                this.changeLocationStateToFailure(exception)
            }
    }

    /** Requests the location to create the looper */
    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        this._client.requestLocationUpdates(
            this._request,
            this._callback,
            Looper.getMainLooper()
        )
        .addOnFailureListener { exception ->
            this.changeLocationStateToFailure(exception)
        }
    }
}