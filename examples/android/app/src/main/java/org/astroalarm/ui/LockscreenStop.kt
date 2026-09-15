package org.astroalarm.ui

object LockscreenStop {
    const val SCALE_MS = 80
    const val PRESSED_SCALE = 0.92f

    fun animateScale(reduceMotion: Boolean): Boolean = !reduceMotion

    fun spoken(action: String, label: String): String {
        val name = label.trim()
        return if (name.isEmpty()) action else "$action $name"
    }
}
