package com.mancel.yann.whereismycar

import android.app.Application
import com.mancel.yann.whereismycar.viewModels.ViewModelFactory

/**
 * Created by Yann MANCEL on 13/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar
 *
 * An [Application] subclass.
 */
class WhereIsMyCarApplication : Application() {

    // FIELDS --------------------------------------------------------------------------------------

    val _viewModelFactory by lazy { ViewModelFactory(this.applicationContext) }

    // METHODS -------------------------------------------------------------------------------------

    // -- Application --

    override fun onCreate() {
        super.onCreate()

        // Enable Debugging for Kotlin Coroutines
        System.setProperty("kotlinx.coroutines.debug", if (BuildConfig.DEBUG) "on" else "off")
    }
}