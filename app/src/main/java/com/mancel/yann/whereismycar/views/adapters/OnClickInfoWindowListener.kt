package com.mancel.yann.whereismycar.views.adapters

import com.google.android.gms.maps.model.Marker

/**
 * Created by Yann MANCEL on 15/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.views.adapters
 */
interface OnClickInfoWindowListener {

    // METHODS -------------------------------------------------------------------------------------

    fun onClickOnWayButton(marker: Marker)

    fun onClickOnDeleteButton(marker: Marker)
}