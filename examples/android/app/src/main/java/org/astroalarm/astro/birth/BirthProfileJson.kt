package org.astroalarm.astro.birth

import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

object BirthProfileJson {
    fun toJson(p: BirthProfile): JSONObject = JSONObject().apply {
        put("id", p.id)
        put("label", p.label)
        put("birthDate", p.birthDate.toString())
        put("birthTime", p.birthTime?.toString() ?: JSONObject.NULL)
        put("timeUnknown", p.timeUnknown)
        put("cityName", p.cityName)
        put("lat", p.lat)
        put("lon", p.lon)
        put("zoneId", p.zoneId)
        put("zoneIsFallback", p.zoneIsFallback)
        put("active", p.active)
    }

    fun fromJson(obj: JSONObject): BirthProfile? {
        val id = obj.optString("id").ifBlank { UUID.randomUUID().toString() }
        val label = obj.optString("label", "Profile")
        val date = runCatching { LocalDate.parse(obj.optString("birthDate")) }.getOrNull() ?: return null
        val timeUnknown = obj.optBoolean("timeUnknown", false)
        val time = if (timeUnknown || obj.isNull("birthTime")) {
            null
        } else {
            runCatching { LocalTime.parse(obj.optString("birthTime")) }.getOrNull()
        }
        return BirthProfile(
            id = id,
            label = label,
            birthDate = date,
            birthTime = time,
            timeUnknown = timeUnknown,
            cityName = obj.optString("cityName", ""),
            lat = obj.optDouble("lat", 0.0),
            lon = obj.optDouble("lon", 0.0),
            zoneId = obj.optString("zoneId", "UTC"),
            zoneIsFallback = obj.optBoolean("zoneIsFallback", false),
            active = obj.optBoolean("active", false),
        )
    }

    fun listToJson(list: List<BirthProfile>): String {
        val arr = JSONArray()
        list.forEach { arr.put(toJson(it)) }
        return arr.toString()
    }

    fun listFromJson(raw: String?): List<BirthProfile> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            (0 until arr.length()).mapNotNull { i ->
                arr.optJSONObject(i)?.let { fromJson(it) }
            }
        }.getOrDefault(emptyList())
    }
}
