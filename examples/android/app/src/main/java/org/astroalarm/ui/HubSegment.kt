package org.astroalarm.ui

import org.astroalarm.astro.settings.DailyChip
import org.astroalarm.astro.settings.SkyChip

object HubSegment {
    val daily = DailyChip.entries
    val sky = SkyChip.entries

    fun dailyIndex(chip: DailyChip): Int = daily.indexOf(chip).coerceAtLeast(0)

    fun skyIndex(chip: SkyChip): Int = sky.indexOf(chip).coerceAtLeast(0)
}
