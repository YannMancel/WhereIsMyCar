package com.mancel.yann.whereismycar.repositories

import com.mancel.yann.whereismycar.api.GoogleMapsService
import com.mancel.yann.whereismycar.models.Location
import com.mancel.yann.whereismycar.states.WayState

/**
 * Created by Yann MANCEL on 15/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.repositories
 *
 * A class which implements [WayRepository].
 */
class GoogleWayRepository : WayRepository {

    // FIELDS --------------------------------------------------------------------------------------

    private val _googleMapsService = GoogleMapsService.googleMapsService

    // METHODS -------------------------------------------------------------------------------------

    override suspend fun getWay(
        origin: String,
        destination: String,
        mode: String,
        key: String
    ) : WayState {
        return try {
            val directions = this._googleMapsService.getDirections(origin, destination, mode, key)

            // Only the first route
            val firstWay = mutableListOf<Location>()
            var isFirstItem = true
            directions.routes?.first()?.legs?.first()?.steps?.forEach {
                // Start location
                if (isFirstItem) {
                    firstWay.add(
                        Location(
                            _latitude = it.startLocation?.lat ?: 0.0,
                            _longitude = it.startLocation?.lng ?: 0.0
                        )
                    )
                    isFirstItem = false
                }

                // End location
                firstWay.add(
                    Location(
                        _latitude = it.endLocation?.lat ?: 0.0,
                        _longitude = it.endLocation?.lng ?: 0.0
                    )
                )
            }

            WayState.Success(firstWay)
        } catch (e: Exception) {
            WayState.Failure(e)
        }
    }
}