package com.mancel.yann.whereismycar.helpers

import android.content.Context

/**
 * Created by Yann MANCEL on 15/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.helpers
 */

// METHODS -----------------------------------------------------------------------------------------

fun getPixelsFromDp(context: Context, dp: Float): Float {
    val scale = context.resources.displayMetrics.density
    return (dp * scale + 0.5F)
}