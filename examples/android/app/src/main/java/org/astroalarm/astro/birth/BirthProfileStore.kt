package org.astroalarm.astro.birth

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BirthProfileStore(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val _profiles = MutableStateFlow(load())
    val profiles: StateFlow<List<BirthProfile>> = _profiles.asStateFlow()

    fun getAll(): List<BirthProfile> = _profiles.value
    fun getById(id: String): BirthProfile? = _profiles.value.firstOrNull { it.id == id }
    fun active(): BirthProfile? = _profiles.value.firstOrNull { it.active } ?: _profiles.value.firstOrNull()

    fun save(profile: BirthProfile) {
        var list = _profiles.value.filter { it.id != profile.id } + profile
        if (profile.active) {
            list = list.map { it.copy(active = it.id == profile.id) }
        }
        if (list.isNotEmpty() && list.none { it.active }) {
            list = list.mapIndexed { i, p -> p.copy(active = i == 0) }
        }
        persist(list)
    }

    fun setActive(id: String) {
        persist(_profiles.value.map { it.copy(active = it.id == id) })
    }

    fun delete(id: String) {
        var list = _profiles.value.filter { it.id != id }
        if (list.isNotEmpty() && list.none { it.active }) {
            list = list.mapIndexed { i, p -> p.copy(active = i == 0) }
        }
        persist(list)
    }

    private fun persist(list: List<BirthProfile>) {
        prefs.edit().putString(KEY, BirthProfileJson.listToJson(list)).apply()
        _profiles.value = list
        runCatching {
            org.astroalarm.widget.NatalAlignWidgetProvider.updateAll(appContext)
            org.astroalarm.widget.NatalChartWidgetProvider.updateAll(appContext)
        }
    }

    private fun load(): List<BirthProfile> =
        BirthProfileJson.listFromJson(prefs.getString(KEY, null))

    companion object {
        private const val PREFS = "os_astro_birth_prefs"
        private const val KEY = "birth_profiles_json"
    }
}
