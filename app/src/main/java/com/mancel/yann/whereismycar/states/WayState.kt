package com.mancel.yann.whereismycar.states

import com.mancel.yann.whereismycar.models.Location
import com.mancel.yann.whereismycar.repositories.GoogleWayRepository
import com.mancel.yann.whereismycar.viewModels.SharedViewModel

/**
 * Created by Yann MANCEL on 19/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.states
 */
sealed class WayState {

    // CLASSES -------------------------------------------------------------------------------------

    /**
     * State:  Success
     * Where:  [GoogleWayRepository.getWay]
     * Why:    Way is a success
     */
    class Success(val _way: List<Location>) : WayState()

    /**
     * State:  Failure
     * Where:  [GoogleWayRepository.getWay]
     * Why:    Way is a failure
     */
    class Failure(val _exception: Exception) : WayState()

    /**
     * State:  Clear
     * Where:  [SharedViewModel.clearWay]
     * Why:    Way is cleared
     */
    object Clear : WayState()
}