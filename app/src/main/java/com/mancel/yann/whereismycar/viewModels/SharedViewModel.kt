package com.mancel.yann.whereismycar.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mancel.yann.whereismycar.liveDatas.LocationLiveData
import com.mancel.yann.whereismycar.models.POI
import com.mancel.yann.whereismycar.states.LocationState

/**
 * Created by Yann MANCEL on 08/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.viewModels
 *
 * A [ViewModel] subclass.
 */
class SharedViewModel :  ViewModel() {

    // FIELDS --------------------------------------------------------------------------------------

    private var _locationState: LocationLiveData? = null
    private var _pointsOfInterest: MutableLiveData<List<POI>>? = null

    // METHODS -------------------------------------------------------------------------------------

    // -- LocationState --

    fun getLocationState(context: Context) : LiveData<LocationState> {
        if (this._locationState == null)  this._locationState = LocationLiveData(context)
        return this._locationState!!
    }

    fun requestUpdateLocationAfterPermission() = this._locationState?.requestUpdateLocation()

    // -- Point of interest --

    fun getPointsOfInterest() : LiveData<List<POI>> {
        if (this._pointsOfInterest == null) this._pointsOfInterest = MutableLiveData<List<POI>>()
        return this._pointsOfInterest!!
    }

    fun addPointOfInterest(poi: POI) {
        val pointsOfInterest = this._pointsOfInterest?.value?.toMutableList() ?: mutableListOf()
        if(!pointsOfInterest.contains(poi)) pointsOfInterest.add(poi)

        this._pointsOfInterest?.value = pointsOfInterest
    }

    fun updatePointOfInterest(id: Long, latitude: Double, longitude: Double) {
        val pointsOfInterest = this._pointsOfInterest?.value?.toMutableList() ?: mutableListOf()

        for ((index, value) in pointsOfInterest.withIndex()) {
            if (value._id == id) {
                pointsOfInterest[index] = POI(id, latitude, longitude)

                // todo - 13/10/2020 - Just the first element
                break
            }
        }

        this._pointsOfInterest?.value = pointsOfInterest
    }
}