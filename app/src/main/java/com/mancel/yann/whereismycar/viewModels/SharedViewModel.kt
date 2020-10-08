package com.mancel.yann.whereismycar.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mancel.yann.whereismycar.liveDatas.LocationLiveData
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

    // METHODS -------------------------------------------------------------------------------------

    // -- LocationState --

    fun getLocationState(context: Context) : LiveData<LocationState> {
        if (this._locationState == null)  this._locationState = LocationLiveData(context)
        return this._locationState as LiveData<LocationState>
    }

    fun requestUpdateLocationAfterPermission() = this._locationState?.requestUpdateLocation()
}