package org.astroalarm.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut

object UxMotion {
    const val FAB_MS = 200
    const val ROW_MS = 180
    const val EMPTY_MS = 120

    fun fabEnter(reduceMotion: Boolean): EnterTransition =
        if (reduceMotion) EnterTransition.None
        else fadeIn(tween(FAB_MS)) + scaleIn(tween(FAB_MS))

    fun fabExit(reduceMotion: Boolean): ExitTransition =
        if (reduceMotion) ExitTransition.None
        else fadeOut(tween(FAB_MS)) + scaleOut(tween(FAB_MS))

    fun emptyEnter(reduceMotion: Boolean): EnterTransition =
        if (reduceMotion) EnterTransition.None else fadeIn(tween(EMPTY_MS))

    fun emptyExit(reduceMotion: Boolean): ExitTransition =
        if (reduceMotion) ExitTransition.None else fadeOut(tween(EMPTY_MS))
}
