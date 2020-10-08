package com.mancel.yann.whereismycar.helpers

import android.app.Activity
import android.view.View

/**
 * Created by Yann MANCEL on 08/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.helpers
 */
object FullScreenHelper {

    /**
     * Hide the system UI in setting the Android fullscreen flags.
     * Expected to be called from [Activity.onWindowFocusChanged].
     * @param activity the Activity on which the full screen mode will be set.
     */
    fun hideSystemUI(activity: Activity) {
        /*
            Fullscreen options:
            See: https://developer.android.com/training/system-ui/immersive.html#sticky

                Lean back:
                    SYSTEM_UI_FLAG_FULLSCREEN
                    SYSTEM_UI_FLAG_HIDE_NAVIGATION

                Immersive:
                    SYSTEM_UI_FLAG_FULLSCREEN
                    SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    SYSTEM_UI_FLAG_IMMERSIVE

                Sticky immersive:
                    SYSTEM_UI_FLAG_FULLSCREEN
                    SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    SYSTEM_UI_FLAG_IMMERSIVE_STICKY
         */
        activity
            .window
            .decorView
            .systemUiVisibility = (
                // Good practice to prevent your layout
                // from resizing when the system bars hide and show
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
    }

    /**
     * Shows the system bars by removing all the flags except for the ones
     * that make the content appear under the system bars.
     */
    fun showSystemUI(activity: Activity) {
        activity
            .window
            .decorView
            .systemUiVisibility = (
                // Good practice to prevent your layout
                // from resizing when the system bars hide and show
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
    }
}