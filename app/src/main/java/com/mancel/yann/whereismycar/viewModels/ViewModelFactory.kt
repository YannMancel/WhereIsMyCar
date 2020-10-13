package com.mancel.yann.whereismycar.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mancel.yann.whereismycar.databases.AppDatabase
import com.mancel.yann.whereismycar.repositories.RoomDatabaseRepository
import java.lang.IllegalArgumentException

/**
 * Created by Yann MANCEL on 13/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.viewModels
 *
 * A class which implements [ViewModelProvider.Factory].
 */
class ViewModelFactory(private val _context: Context) : ViewModelProvider.Factory {

    // METHODS -------------------------------------------------------------------------------------

    // -- ViewModelProvider.Factory interface --

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            val database = AppDatabase.getDatabase(this._context)
            val repository = RoomDatabaseRepository(database.poiDAO())
            return SharedViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}