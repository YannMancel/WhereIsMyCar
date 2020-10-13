package com.mancel.yann.whereismycar.helpers

import android.util.Log
import com.mancel.yann.whereismycar.BuildConfig

/**
 * Created by Yann MANCEL on 13/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.helpers
 */

// METHODS -----------------------------------------------------------------------------------------

fun <T: Any> T.logCoroutineOnDebug(msg: String? = null) {
    if (BuildConfig.DEBUG)
        Log.d(
            this.javaClass.simpleName,
            "Running on: [${Thread.currentThread().name}] ${msg ?: ""}"
        )
}