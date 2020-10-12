package com.mancel.yann.whereismycar.helpers

import android.view.View
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Yann MANCEL on 08/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.helpers
 */

// METHODS -----------------------------------------------------------------------------------------

fun showMessageWithSnackbar(
    view: View,
    message: String,
    textButton: String? = null,
    actionOnClick: View.OnClickListener? = null
) {
    Snackbar
        .make(view, message, Snackbar.LENGTH_SHORT)
        .setAction(textButton, actionOnClick)
        .show()
}