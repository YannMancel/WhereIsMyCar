package com.mancel.yann.whereismycar.databases

import androidx.room.*
import com.mancel.yann.whereismycar.models.POI
import kotlinx.coroutines.flow.Flow

/**
 * Created by Yann MANCEL on 13/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.databases
 */
@Dao
interface PoiDAO {

    // METHODS -------------------------------------------------------------------------------------

    // -- Create --

    @Insert
    suspend fun insertPointsOfInterest(vararg pointsOfInterest: POI): List<Long>

    // -- Read --

    @Query("SELECT * FROM point_of_interest")
    fun getPointsOfInterest(): Flow<List<POI>>

    // -- Update --

    @Update
    suspend fun updatePointsOfInterest(vararg pointsOfInterest: POI): Int

    // -- Delete --

    @Delete
    suspend fun removePointsOfInterest(vararg pointsOfInterest: POI): Int
}