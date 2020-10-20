package com.mancel.yann.whereismycar.viewModels

import android.content.Context
import androidx.lifecycle.*
import com.mancel.yann.whereismycar.R
import com.mancel.yann.whereismycar.helpers.logCoroutineOnDebug
import com.mancel.yann.whereismycar.liveDatas.LocationLiveData
import com.mancel.yann.whereismycar.models.POI
import com.mancel.yann.whereismycar.repositories.DatabaseRepository
import com.mancel.yann.whereismycar.repositories.WayRepository
import com.mancel.yann.whereismycar.states.LocationState
import com.mancel.yann.whereismycar.states.WayState
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
    private val _context: Context,
    private val _databaseRepository: DatabaseRepository,
    private val _wayRepository: WayRepository,
    private val _backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO
) :  ViewModel() {

    // FIELDS --------------------------------------------------------------------------------------

    // -- LiveData --

    private var _locationState: LocationLiveData? = null

    private val _pointsOfInterest by lazy {
        this._databaseRepository.getPointsOfInterest().asLiveData()
    }

    private var _wayState: MutableLiveData<WayState>? = null

    // -- Key --

    private val _googleMapsKey by lazy { this._context.getString(R.string.google_maps_key) }

    // -- Error handler --

    private val _errorHandler = CoroutineExceptionHandler { _, throwable ->
        this.logCoroutineOnDebug(throwable.message)
    }

    companion object {
        private const val COROUTINE_ADD_DATA = "Add data in database"
        private const val COROUTINE_UPDATE_DATA = "Update data in database"
        private const val COROUTINE_REMOVE_DATA = "Remove data in database"
        private const val COROUTINE_WAY = "Way from Google Maps"
    }

    // METHODS -------------------------------------------------------------------------------------

    // -- CoroutineContext --

    private fun getCoroutineContext(name: String): CoroutineContext =
        this._backgroundDispatcher + this._errorHandler + CoroutineName(name)

    // -- LocationState --

    fun getLocationState() : LiveData<LocationState> {
        if (this._locationState == null)  this._locationState = LocationLiveData(this._context)
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
            this@SharedViewModel.logCoroutineOnDebug("Launch finished")
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
            this@SharedViewModel.logCoroutineOnDebug("Launch finished")
        }

    fun removePointOfInterest(poi: POI) =
        this.viewModelScope.launch(
            context = this.getCoroutineContext(name = COROUTINE_REMOVE_DATA)
        ) {
            this@SharedViewModel.logCoroutineOnDebug("Launch started")
            this@SharedViewModel._databaseRepository.removePointsOfInterest(poi)
            this@SharedViewModel.logCoroutineOnDebug("Launch finished")
        }

    // -- Way --

    fun getWayState(): LiveData<WayState> {
        if (this._wayState == null) this._wayState = MutableLiveData()
        return this._wayState!!
    }

    fun buildWay(origin: String, destination: String, mode: String) =
        this.viewModelScope.launch(
            context = this.getCoroutineContext(name = COROUTINE_WAY)
        ) {
            this@SharedViewModel.logCoroutineOnDebug("Launch started")

            if (this@SharedViewModel._wayState == null) {
                this@SharedViewModel.logCoroutineOnDebug("Launch finished without request")
                return@launch
            }

            val deferredWay = async {
                this@SharedViewModel._wayRepository.getWay(
                    origin, destination, mode, this@SharedViewModel._googleMapsKey
                )
            }
            this@SharedViewModel._wayState?.postValue(deferredWay.await())
            this@SharedViewModel.logCoroutineOnDebug("Launch finished")
        }
}