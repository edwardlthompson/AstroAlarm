package org.astroalarm.astro.birth

import java.util.Locale

/**
 * Builds a self-contained clipboard prompt for any on-device agent.
 * Returns null when the chart lacks place, ascendant, or body positions.
 */
object NatalAgentPrompt {
    fun build(profile: BirthProfile, chart: NatalChart): String? {
        if (!canCopy(profile, chart)) return null
        val asc = chart.ascendant!!
        val table = buildString {
            appendLine("| Body | Sign | Degree | House | Ecliptic ° |")
            appendLine("|---|---|---|---|---|")
            for ((body, point) in chart.planets) {
                val house = WholeSignHouses.houseOf(point.longitudeDeg, asc) ?: continue
                appendLine(
                    "| ${bodyLabel(body)} | ${point.sign.englishName} | " +
                        "${fmt(point.degreeInSign)}° | $house | " +
                        "${fmt(point.longitudeDeg)} |"
                )
            }
            appendLine()
            appendLine(
                "Ascendant: ${fmt(asc.longitudeDeg)}° " +
                    "(whole-sign houses; house 1 = Ascendant sign)"
            )
        }
        val timePart = when {
            profile.timeUnknown || profile.birthTime == null -> "time unknown"
            else -> "${pad(profile.birthTime.hour)}:${pad(profile.birthTime.minute)}"
        }
        return buildString {
            appendLine("You are helping with a tropical natal chart personality profile.")
            appendLine("Use ONLY the measurements below (positions, degrees, houses, and aspects you infer from them).")
            appendLine("Return a clear, accurate personality profile in plain prose the user can paste back into AstroAlarm.")
            appendLine("Do not invent birth data. Do not refuse because you are not an astrologer — reason from the table.")
            appendLine()
            appendLine("Privacy note: this prompt was copied on the user's device. Pasting into a cloud agent leaves the device.")
            appendLine()
            appendLine(
                "Birth (local): ${profile.birthDate} $timePart at ${profile.cityName} " +
                    "(${profile.lat}, ${profile.lon}, ${profile.zoneId})"
            )
            appendLine()
            append(table)
        }.trimEnd()
    }

    fun canCopy(profile: BirthProfile?, chart: NatalChart?): Boolean {
        if (profile == null || chart == null) return false
        if (!profile.hasPlace || !profile.canComputeAscendant) return false
        if (chart.ascendant == null || chart.planets.isEmpty()) return false
        return true
    }

    private fun bodyLabel(body: NatalBody): String =
        body.name.lowercase(Locale.US).replaceFirstChar { it.uppercase(Locale.US) }

    private fun pad(n: Int): String = n.toString().padStart(2, '0')

    private fun fmt(v: Double): String = String.format(Locale.US, "%.2f", v)
}
