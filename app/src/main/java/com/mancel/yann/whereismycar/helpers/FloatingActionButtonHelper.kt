package com.mancel.yann.whereismycar.helpers

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Created by Yann MANCEL on 20/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.helpers
 */

// METHODS -----------------------------------------------------------------------------------------

fun FloatingActionButton.animateWithTranslationYAndStartAction(
    offsetY: Float,
    startAction: FloatingActionButton.() -> Unit
) {
    ViewCompat
        .animate(this)
        .translationY(offsetY)
        .withStartAction { this.startAction() }
        .withEndAction {
            ObjectAnimator.ofFloat(
                this,
                View.ROTATION,
                0F, 180F
            ).apply {
                duration = 500
                repeatCount = 2
                repeatMode = ValueAnimator.REVERSE
                start()
            }
        }
}

fun FloatingActionButton.animateWithTranslationYAndEndAction(
    offsetY: Float,
    endAction: FloatingActionButton.() -> Unit
) {
    ViewCompat
        .animate(this)
        .translationY(offsetY)
        .withEndAction { this.endAction() }
}