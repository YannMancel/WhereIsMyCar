package com.mancel.yann.whereismycar.states

import com.mancel.yann.whereismycar.views.fragments.MapFragment

/**
 * Created by Yann MANCEL on 08/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.states
 */
sealed class LocationState {

    // CLASSES -------------------------------------------------------------------------------------

    /**
     * State:  Success
     * Where:  [MapFragment.updateUIWithLocationEvents]
     * Why:    Location is a success
     */
    class Success(val _location: String) : LocationState()

    /**
     * State:  Failure
     * Where:  [MapFragment.updateUIWithLocationEvents]
     * Why:    Location is a failure
     */
    class Failure(val _exception: Exception) : LocationState()
}