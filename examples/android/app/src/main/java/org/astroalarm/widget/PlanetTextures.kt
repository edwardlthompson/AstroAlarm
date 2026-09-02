package org.astroalarm.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dev.foss.goldenpath.R
import org.astroalarm.sol.PlanetBody

object PlanetTextures {
    fun get(context: Context, body: PlanetBody): Bitmap? {
        val id = when (body) {
            PlanetBody.MERCURY -> R.drawable.mercury_map
            PlanetBody.VENUS -> R.drawable.venus_map
            PlanetBody.EARTH -> R.drawable.earth_daymap
            PlanetBody.MARS -> R.drawable.mars_map
            PlanetBody.JUPITER -> R.drawable.jupiter_map
            PlanetBody.SATURN -> R.drawable.saturn_map
            PlanetBody.URANUS -> R.drawable.uranus_map
            PlanetBody.NEPTUNE -> R.drawable.neptune_map
        }
        return runCatching { BitmapFactory.decodeResource(context.resources, id) }.getOrNull()
    }
}
