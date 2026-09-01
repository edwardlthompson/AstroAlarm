package org.astroalarm.astro.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.astroalarm.ui.AlarmViewMode
import org.astroalarm.widget.Astro3DClockWidgetProvider
import org.astroalarm.widget.AstroClockWidgetProvider
import org.astroalarm.widget.AstroUpcomingWidgetProvider
import org.astroalarm.widget.SolarTermWidgetProvider

class AstroDisplayPreferences(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _showZodiac2D = MutableStateFlow(prefs.getBoolean(KEY_SHOW_ZODIAC_2D, true))
    val showZodiac2D: StateFlow<Boolean> = _showZodiac2D.asStateFlow()

    private val _showZodiac3D = MutableStateFlow(prefs.getBoolean(KEY_SHOW_ZODIAC_3D, true))
    val showZodiac3D: StateFlow<Boolean> = _showZodiac3D.asStateFlow()

    private val _showEventTimes2D = MutableStateFlow(prefs.getBoolean(KEY_SHOW_EVENT_TIMES_2D, true))
    val showEventTimes2D: StateFlow<Boolean> = _showEventTimes2D.asStateFlow()

    private val _showMonthTicks2D = MutableStateFlow(prefs.getBoolean(KEY_SHOW_MONTH_TICKS_2D, false))
    val showMonthTicks2D: StateFlow<Boolean> = _showMonthTicks2D.asStateFlow()

    private val _showSolarTermsYear = MutableStateFlow(prefs.getBoolean(KEY_SHOW_SOLAR_TERMS_YEAR, false))
    val showSolarTermsYear: StateFlow<Boolean> = _showSolarTermsYear.asStateFlow()

    private val _solarTermTraditional = MutableStateFlow(prefs.getBoolean(KEY_SOLAR_TERM_TRADITIONAL, false))
    val solarTermTraditional: StateFlow<Boolean> = _solarTermTraditional.asStateFlow()

    private val _solarTermLocalSeasons = MutableStateFlow(prefs.getBoolean(KEY_SOLAR_TERM_LOCAL_SEASONS, false))
    val solarTermLocalSeasons: StateFlow<Boolean> = _solarTermLocalSeasons.asStateFlow()

    private val _alarmViewMode = MutableStateFlow(
        if (prefs.getString(KEY_ALARM_VIEW_MODE, AlarmViewMode.NextDue.name) == AlarmViewMode.Grouped.name)
            AlarmViewMode.Grouped else AlarmViewMode.NextDue
    )
    val alarmViewMode: StateFlow<AlarmViewMode> = _alarmViewMode.asStateFlow()

    fun setShowZodiac2D(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_ZODIAC_2D, enabled).apply()
        _showZodiac2D.value = enabled
        AstroClockWidgetProvider.updateAll(context)
    }

    fun setShowZodiac3D(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_ZODIAC_3D, enabled).apply()
        _showZodiac3D.value = enabled
        Astro3DClockWidgetProvider.updateAll(context)
    }

    fun setShowEventTimes2D(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_EVENT_TIMES_2D, enabled).apply()
        _showEventTimes2D.value = enabled
        AstroClockWidgetProvider.updateAll(context)
    }

    fun setShowMonthTicks2D(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_MONTH_TICKS_2D, enabled).apply()
        _showMonthTicks2D.value = enabled
        AstroClockWidgetProvider.updateAll(context)
    }

    fun setAlarmViewMode(mode: AlarmViewMode) {
        prefs.edit().putString(KEY_ALARM_VIEW_MODE, mode.name).apply()
        _alarmViewMode.value = mode
        AstroUpcomingWidgetProvider.updateAll(context)
    }

    fun setShowSolarTermsYear(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_SOLAR_TERMS_YEAR, enabled).apply()
        _showSolarTermsYear.value = enabled
        SolarTermWidgetProvider.updateAll(context)
    }

    fun setSolarTermTraditional(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOLAR_TERM_TRADITIONAL, enabled).apply()
        _solarTermTraditional.value = enabled
        SolarTermWidgetProvider.updateAll(context)
    }

    fun setSolarTermLocalSeasons(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOLAR_TERM_LOCAL_SEASONS, enabled).apply()
        _solarTermLocalSeasons.value = enabled
        SolarTermWidgetProvider.updateAll(context)
    }

    fun isShowZodiac2D(): Boolean = _showZodiac2D.value
    fun isShowZodiac3D(): Boolean = _showZodiac3D.value
    fun isShowEventTimes2D(): Boolean = _showEventTimes2D.value
    fun isShowMonthTicks2D(): Boolean = _showMonthTicks2D.value
    fun isShowSolarTermsYear(): Boolean = _showSolarTermsYear.value
    fun isSolarTermTraditional(): Boolean = _solarTermTraditional.value
    fun isSolarTermLocalSeasons(): Boolean = _solarTermLocalSeasons.value
    fun getAlarmViewMode(): AlarmViewMode = _alarmViewMode.value

    companion object {
        private const val PREFS_NAME = "astro_display_prefs"
        private const val KEY_SHOW_ZODIAC_2D = "show_zodiac_2d"
        private const val KEY_SHOW_ZODIAC_3D = "show_zodiac_3d"
        private const val KEY_SHOW_EVENT_TIMES_2D = "show_event_times_2d"
        private const val KEY_SHOW_MONTH_TICKS_2D = "show_month_ticks_2d"
        private const val KEY_SHOW_SOLAR_TERMS_YEAR = "show_solar_terms_year"
        private const val KEY_SOLAR_TERM_TRADITIONAL = "solar_term_traditional"
        private const val KEY_SOLAR_TERM_LOCAL_SEASONS = "solar_term_local_seasons"
        private const val KEY_ALARM_VIEW_MODE = "alarm_view_mode"
    }
}
