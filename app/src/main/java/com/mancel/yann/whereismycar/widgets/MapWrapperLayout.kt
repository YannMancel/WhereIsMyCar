package com.mancel.yann.whereismycar.widgets

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.mancel.yann.whereismycar.views.adapters.InfoWindowAdapter

/**
 * Created by Yann MANCEL on 14/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.widgets
 *
 * A [ConstraintLayout] subclass.
 */
class MapWrapperLayout(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {

    // See: http://androidlearningtutorials.com/blog_details.php?article_id=8

    // FIELDS --------------------------------------------------------------------------------------

    private var _map: GoogleMap? = null

    /**
     * Vertical offset in pixels between the bottom edge of our InfoWindow
     * and the marker position (by default it's bottom edge too).
     * It's a good idea to use custom markers and also the InfoWindow frame,
     * because we probably can't rely on the sizes of the default marker and frame.
     */
    private var _bottomOffsetPixels = 0F

    private var _marker: Marker? = null

    private var _infoWindow: View? = null

    // METHODS -------------------------------------------------------------------------------------

    // -- ConstraintLayout --

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        var isDispatched = false

        // Make sure that the mInfoWindow is shown and we have all the needed references
        if (this._marker != null
            && this._marker!!.isInfoWindowShown
            && this._map != null
            && this._infoWindow != null
        ) {
            // Get a marker position on the screen
            val point: Point = this._map!!.projection.toScreenLocation(this._marker!!.position)

            // Make a copy of the MotionEvent and adjust it's location
            // so it is relative to the _infoWindow left top corner
            val copyEv = MotionEvent.obtain(ev)

            copyEv.offsetLocation(
                -point.x.toFloat() + (this._infoWindow!!.width.toFloat() / 2F),
                (-point.y + this._infoWindow!!.height).toFloat() + this._bottomOffsetPixels
            )

            // Dispatch the adjusted MotionEvent to the mInfoWindow
            isDispatched = this._infoWindow!!.dispatchTouchEvent(copyEv)
        }

        // If the _infoWindow consumed the touch event, then just return true.
        // Otherwise pass this event to the super class and return it's result
        return isDispatched || super.dispatchTouchEvent(ev)
    }

    // -- Init --

    /**
     * Must be called before we can route the touch events
     * @param map                   a [GoogleMap]
     * @param bottomOffsetPixels    Vertical offset in pixels between the bottom edge of our InfoWindow
     *                              and the marker position (by default it's bottom edge too).
     */
    fun init(map: GoogleMap?, bottomOffsetPixels: Float = 0F) {
        this._map = map
        this._bottomOffsetPixels = bottomOffsetPixels
    }

    // -- Marker and InfoWindow --

    /**
     * Best to be called from either
     * - [InfoWindowAdapter.getInfoWindow] or
     * - [InfoWindowAdapter.getInfoContents]
     */
    fun setMarkerWithInfoWindow(marker: Marker?, infoWindow: View?) {
        this._marker = marker
        this._infoWindow = infoWindow
    }

    fun clearMarkerWithInfoWindow() {
        this._marker = null
        this._infoWindow = null
    }
}