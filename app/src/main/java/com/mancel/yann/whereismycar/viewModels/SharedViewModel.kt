package com.mancel.yann.whereismycar.viewModels

import android.content.Context
import androidx.lifecycle.*
import com.mancel.yann.whereismycar.helpers.logCoroutineOnDebug
import com.mancel.yann.whereismycar.liveDatas.LocationLiveData
import com.mancel.yann.whereismycar.models.Location
import com.mancel.yann.whereismycar.models.POI
import com.mancel.yann.whereismycar.repositories.DatabaseRepository
import com.mancel.yann.whereismycar.repositories.WayRepository
import com.mancel.yann.whereismycar.states.LocationState
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by Yann MANCEL on 08/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.viewModels
 *
 * A [ViewModel] subclass.
 */
class SharedViewModel(
    private val _databaseRepository: DatabaseRepository,
    private val _wayRepository: WayRepository,
    private val _backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO
) :  ViewModel() {

    // FIELDS --------------------------------------------------------------------------------------

    private var _locationState: LocationLiveData? = null

    private val _pointsOfInterest by lazy {
        this._databaseRepository.getPointsOfInterest().asLiveData()
    }

    private val _errorHandler = CoroutineExceptionHandler { _, throwable ->
        this.logCoroutineOnDebug(throwable.message)
    }

    companion object {
        private const val COROUTINE_ADD_DATA = "Add data in database"
        private const val COROUTINE_UPDATE_DATA = "Update data in database"
        private const val COROUTINE_REMOVE_DATA = "Remove data in database"
    }

    // METHODS -------------------------------------------------------------------------------------

    // -- CoroutineContext --

    private fun getCoroutineContext(name: String): CoroutineContext =
        this._backgroundDispatcher + this._errorHandler + CoroutineName(name)

    // -- LocationState --

    fun getLocationState(context: Context) : LiveData<LocationState> {
        if (this._locationState == null)  this._locationState = LocationLiveData(context)
        return this._locationState!!
    }

    fun requestUpdateLocationAfterPermission() = this._locationState?.requestUpdateLocation()

    // -- Point of interest --

    fun getPointsOfInterest() : LiveData<List<POI>> = this._pointsOfInterest

    fun addPointOfInterest(poi: POI) =
        this.viewModelScope.launch(
            context = this.getCoroutineContext(name = COROUTINE_ADD_DATA)
        ) {
            this@SharedViewModel.logCoroutineOnDebug("Launch started")
            this@SharedViewModel._databaseRepository.insertPointsOfInterest(poi)
        }

    fun updatePointOfInterest(id: Long, latitude: Double, longitude: Double) =
        this.viewModelScope.launch(
            context = this.getCoroutineContext(name = COROUTINE_UPDATE_DATA)
        ) {
            this@SharedViewModel.logCoroutineOnDebug("Launch started")

            val elementToUpdate =
                this@SharedViewModel._pointsOfInterest.value?.find { it._id == id }

            // No element
            elementToUpdate ?: return@launch

            val elementAfterUpdate =
                elementToUpdate.copy(_latitude = latitude, _longitude = longitude)

            this@SharedViewModel._databaseRepository.updatePointsOfInterest(elementAfterUpdate)
        }

    fun removePointOfInterest(poi: POI) =
        this.viewModelScope.launch(
            context = this.getCoroutineContext(name = COROUTINE_REMOVE_DATA)
        ) {
            this@SharedViewModel.logCoroutineOnDebug("Launch started")
            this@SharedViewModel._databaseRepository.removePointsOfInterest(poi)
        }

    // -- Way --

    fun buildWay(currentLocation: Location, poi: POI, way: String) {
        // todo - 15/10/202 - Request to Google Maps
    }
}