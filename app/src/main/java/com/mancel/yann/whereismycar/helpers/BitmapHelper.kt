package com.mancel.yann.whereismycar.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap

/**
 * Created by Yann MANCEL on 12/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.helpers
 */

// METHODS -----------------------------------------------------------------------------------------

fun getBitmapFromDrawableResource(context: Context, @DrawableRes drawableId: Int): Bitmap {
    return when (val drawable = ContextCompat.getDrawable(context, drawableId)) {
        is BitmapDrawable -> BitmapFactory.decodeResource(context.resources, drawableId)
        is VectorDrawable -> drawable.toBitmap()
        else -> throw IllegalArgumentException("unsupported drawable type")
    }
}
