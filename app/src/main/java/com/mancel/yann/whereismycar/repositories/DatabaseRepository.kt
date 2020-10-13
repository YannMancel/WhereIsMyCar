package com.mancel.yann.whereismycar.repositories

import com.mancel.yann.whereismycar.models.POI
import kotlinx.coroutines.flow.Flow

/**
 * Created by Yann MANCEL on 13/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.repositories
 */
interface DatabaseRepository {

    // METHODS -------------------------------------------------------------------------------------

    // -- Create --

    suspend fun insertPointsOfInterest(vararg pointsOfInterest: POI): List<Long>

    // -- Read --

    fun getPointsOfInterest(): Flow<List<POI>>

    // -- Update --

    suspend fun updatePointsOfInterest(vararg pointsOfInterest: POI): Int

    // -- Delete --

    suspend fun removePointsOfInterest(vararg pointsOfInterest: POI): Int
}