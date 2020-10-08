package com.mancel.yann.whereismycar.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mancel.yann.whereismycar.states.LocationState
import java.lang.Exception

/**
 * Created by Yann MANCEL on 08/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.viewModels
 *
 * A [ViewModel] subclass.
 */
class SharedViewModel :  ViewModel() {

    // FIELDS --------------------------------------------------------------------------------------

    private val _locationState = MutableLiveData<LocationState>()

    // METHODS -------------------------------------------------------------------------------------

    // -- LocationState --

    fun getLocationState() : LiveData<LocationState> = this._locationState

    private fun changeLocationStateToSuccess(location: String) {
        this._locationState.value = LocationState.Success(location)
    }

    private fun changeLocationStateToFailure(exception: Exception) {
        this._locationState.value = LocationState.Failure(exception)
    }
}