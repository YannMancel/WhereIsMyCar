package com.mancel.yann.whereismycar.views.adapters

import android.view.MotionEvent
import android.view.View
import com.google.android.gms.maps.model.Marker

/**
 * Created by Yann MANCEL on 14/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.views.adapters
 *
 * An abstract class which implements [View.OnTouchListener].
 */
abstract class OnInfoWindowElemTouchListener : View.OnTouchListener {

    // FIELDS --------------------------------------------------------------------------------------

    lateinit var _marker: Marker

    // METHODS -------------------------------------------------------------------------------------

    protected abstract fun onClickConfirmed(marker: Marker)

    // -- View.OnTouchListener --

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        // Limits to an only one action to avoid the multiple event
        // (ex: click on button is in reality 2 events)
        if (event!!.actionMasked == MotionEvent.ACTION_DOWN) {
            onClickConfirmed(this._marker)
        }
        return false
    }
}