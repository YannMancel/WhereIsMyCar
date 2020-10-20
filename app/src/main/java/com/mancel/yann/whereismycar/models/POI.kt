package com.mancel.yann.whereismycar.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Yann MANCEL on 12/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.models
 */

// todo - 13/10/2020 - Add indices on latitude and longitude (unique)

@Entity(tableName = "point_of_interest")
data class POI(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val _id: Long = 0L,
    @ColumnInfo(name = "latitude") val _latitude: Double,
    @ColumnInfo(name = "longitude") val _longitude: Double
) {

    // METHODS -------------------------------------------------------------------------------------

    fun getLocationToGoogleMapsRequest() = "${this._latitude},${this._longitude}"
}