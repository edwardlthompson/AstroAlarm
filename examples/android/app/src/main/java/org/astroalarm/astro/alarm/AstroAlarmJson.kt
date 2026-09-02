package org.astroalarm.astro.alarm

import org.json.JSONArray
import org.json.JSONObject
import org.astroalarm.astro.model.AstroAlarm
import java.time.DayOfWeek
import java.util.UUID

object AstroAlarmJson {
    fun toJson(alarm: AstroAlarm): JSONObject {
        val obj = JSONObject()
        obj.put("id", alarm.id)
        obj.put("label", alarm.label)
        obj.put("enabled", alarm.enabled)
        obj.put("toneEnabled", alarm.toneEnabled)
        obj.put("toneUri", alarm.toneUri ?: JSONObject.NULL)
        obj.put("ttsEnabled", alarm.ttsEnabled)
        obj.put("vibrateEnabled", alarm.vibrateEnabled)
        obj.put("snoozeMinutes", alarm.snoozeMinutes)
        obj.put("mathUnlockEnabled", alarm.mathUnlockEnabled)
        obj.put("lastFiredEpochMs", alarm.lastFiredEpochMs)
        val daysArr = JSONArray()
        alarm.daysOfWeek.forEach { daysArr.put(it.name) }
        obj.put("daysOfWeek", daysArr)
        obj.put("target", AstroAlarmTargetJson.write(alarm.target))
        return obj
    }

    fun fromJson(obj: JSONObject): AstroAlarm? {
        val id = obj.optString("id").takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
        val label = obj.optString("label", "Alarm")
        val enabled = obj.optBoolean("enabled", true)
        val toneEnabled = obj.optBoolean("toneEnabled", true)
        val toneUri = if (obj.isNull("toneUri")) null else obj.optString("toneUri")
        val ttsEnabled = obj.optBoolean("ttsEnabled", true)
        val vibrateEnabled = obj.optBoolean("vibrateEnabled", true)
        val snoozeMinutes = obj.optInt("snoozeMinutes", 10)
        val mathUnlock = obj.optBoolean("mathUnlockEnabled", false)
        val lastFired = obj.optLong("lastFiredEpochMs", 0L)
        val days = mutableSetOf<DayOfWeek>()
        val daysArr = obj.optJSONArray("daysOfWeek")
        if (daysArr != null) {
            for (i in 0 until daysArr.length()) {
                runCatching { DayOfWeek.valueOf(daysArr.optString(i)) }.getOrNull()?.let { days.add(it) }
            }
        }
        val targetObj = obj.optJSONObject("target") ?: return null
        val target = AstroAlarmTargetJson.read(targetObj) ?: return null
        return AstroAlarm(
            id = id,
            label = label,
            enabled = enabled,
            target = target,
            daysOfWeek = days,
            toneEnabled = toneEnabled,
            toneUri = toneUri,
            ttsEnabled = ttsEnabled,
            vibrateEnabled = vibrateEnabled,
            snoozeMinutes = snoozeMinutes,
            mathUnlockEnabled = mathUnlock,
            lastFiredEpochMs = lastFired
        )
    }
}
