package org.astroalarm.ui

import android.content.Context
import android.provider.Settings

object ReduceMotion {
    fun enabled(context: Context): Boolean {
        val cr = context.contentResolver
        val animator = Settings.Global.getFloat(cr, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        val transition = Settings.Global.getFloat(cr, Settings.Global.TRANSITION_ANIMATION_SCALE, 1f)
        return isReduced(animator, transition)
    }

    fun isReduced(animatorScale: Float, transitionScale: Float): Boolean =
        animatorScale == 0f || transitionScale == 0f
}
