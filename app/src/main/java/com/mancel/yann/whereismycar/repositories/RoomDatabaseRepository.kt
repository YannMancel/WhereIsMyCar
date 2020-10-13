package com.mancel.yann.whereismycar.repositories

import com.mancel.yann.whereismycar.databases.PoiDAO
import com.mancel.yann.whereismycar.models.POI
import kotlinx.coroutines.flow.Flow

/**
 * Created by Yann MANCEL on 13/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.repositories
 *
 * A class which implements [DatabaseRepository].
 */
class RoomDatabaseRepository(
    private val _poiDAO: PoiDAO
) : DatabaseRepository {

    // METHODS -------------------------------------------------------------------------------------

    // -- Create --

    override suspend fun insertPointsOfInterest(vararg pointsOfInterest: POI): List<Long> =
        this._poiDAO.insertPointsOfInterest(*pointsOfInterest)

    // -- Read --

    override fun getPointsOfInterest(): Flow<List<POI>> =
        this._poiDAO.getPointsOfInterest()

    // -- Update --

    override suspend fun updatePointsOfInterest(vararg pointsOfInterest: POI): Int =
        this._poiDAO.updatePointsOfInterest(*pointsOfInterest)

    // -- Delete --

    override suspend fun removePointsOfInterest(vararg pointsOfInterest: POI): Int =
        this._poiDAO.removePointsOfInterest(*pointsOfInterest)
}