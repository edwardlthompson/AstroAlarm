package org.astroalarm.astro.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class DailyChip { TwoD, ThreeD }
enum class SkyChip { Yearly, Sol, Chart }

class AstroNavPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private val _daily = MutableStateFlow(readDaily())
    val dailyChip: StateFlow<DailyChip> = _daily.asStateFlow()

    private val _sky = MutableStateFlow(readSky())
    val skyChip: StateFlow<SkyChip> = _sky.asStateFlow()

    fun setDailyChip(chip: DailyChip) {
        prefs.edit().putString(KEY_DAILY, chip.name).apply()
        _daily.value = chip
    }

    fun setSkyChip(chip: SkyChip) {
        prefs.edit().putString(KEY_SKY, chip.name).apply()
        _sky.value = chip
    }

    private fun readDaily(): DailyChip =
        runCatching { DailyChip.valueOf(prefs.getString(KEY_DAILY, DailyChip.TwoD.name)!!) }
            .getOrDefault(DailyChip.TwoD)

    private fun readSky(): SkyChip =
        runCatching { SkyChip.valueOf(prefs.getString(KEY_SKY, SkyChip.Yearly.name)!!) }
            .getOrDefault(SkyChip.Yearly)

    companion object {
        private const val PREFS = "astro_display_prefs"
        private const val KEY_DAILY = "daily_chip"
        private const val KEY_SKY = "sky_chip"
    }
}
