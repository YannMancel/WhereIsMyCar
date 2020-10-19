package com.mancel.yann.whereismycar.repositories

import com.mancel.yann.whereismycar.states.WayState

/**
 * Created by Yann MANCEL on 15/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.repositories
 */
interface WayRepository {

    // METHODS -------------------------------------------------------------------------------------

    suspend fun getWay(
        origin: String,
        destination: String,
        mode: String,
        key: String
    ) : WayState
}