package com.mancel.yann.whereismycar.views.adapters

import android.app.Activity
import android.content.Context
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.mancel.yann.whereismycar.R
import com.mancel.yann.whereismycar.widgets.MapWrapperLayout
import kotlinx.android.synthetic.main.marker.view.*

/**
 * Created by Yann MANCEL on 14/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.views.adapters
 *
 * A class which implements [GoogleMap.InfoWindowAdapter].
 */
class InfoWindowAdapter(
    private val _context: Context,
    private val _wrapperLayout: MapWrapperLayout,
    private val _callback: OnClickInfoWindowListener
) : GoogleMap.InfoWindowAdapter {

    // FIELDS --------------------------------------------------------------------------------------

    private val _view: View =
        (this._context as Activity).layoutInflater.inflate(R.layout.marker, null)

    private lateinit var _wayListener: OnInfoWindowElemTouchListener
    private lateinit var _deleteListener: OnInfoWindowElemTouchListener

    // CONSTRUCTORS --------------------------------------------------------------------------------

    init {
        this.configureActionForWayButton()
        this.configureActionForDeleteButton()
    }

    // METHODS -------------------------------------------------------------------------------------

    // -- GoogleMap.InfoWindowAdapter interface --

    override fun getInfoWindow(marker: Marker?): View? = null

    override fun getInfoContents(marker: Marker?): View? {
        marker ?: return null
        this.updateUI(marker)
        return this._view
    }

    // -- Action --

    private fun configureActionForWayButton() {
        // Way
        this._wayListener = object : OnInfoWindowElemTouchListener() {
            override fun onClickConfirmed(marker: Marker) {
                this@InfoWindowAdapter._callback.onClickOnWayButton(marker)
            }
        }
        this._view.marker_button_way.setOnTouchListener(this._wayListener)
    }

    private fun configureActionForDeleteButton() {
        // Way
        this._deleteListener = object : OnInfoWindowElemTouchListener() {
            override fun onClickConfirmed(marker: Marker) {
                this@InfoWindowAdapter._callback.onClickOnDeleteButton(marker)
            }
        }
        this._view.marker_button_delete.setOnTouchListener(this._deleteListener)
    }

    // -- UI --

    private fun updateUI(marker: Marker) {
        // Latitude
        this._view.marker_latitude.text =
            this._context.getString(R.string.latitude, marker.position.latitude)

        // Longitude
        this._view.marker_longitude.text =
            this._context.getString(R.string.longitude, marker.position.longitude)

        // Listeners
        this._wayListener._marker = marker
        this._deleteListener._marker = marker

        // Event of MapWrapperLayout (InfoWindow)
        this._wrapperLayout.setMarkerWithInfoWindow(marker, this._view)
    }
}