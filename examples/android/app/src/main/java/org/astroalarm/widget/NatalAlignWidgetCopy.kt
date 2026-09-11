package org.astroalarm.widget

import org.astroalarm.astro.birth.BirthChartCalculator
import org.astroalarm.astro.birth.BirthProfile
import org.astroalarm.astro.birth.NatalCompoundNext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object NatalAlignWidgetCopy {
    fun lines(
        profile: BirthProfile?,
        now: Instant = Instant.now(),
        zone: ZoneId = ZoneId.systemDefault(),
        empty: String,
    ): Triple<String, String, String> {
        if (profile == null) return Triple(empty, "", "")
        val chart = BirthChartCalculator.compute(profile) ?: return Triple(empty, "", "")
        val fmt = DateTimeFormatter.ofPattern("EEE HH:mm", Locale.getDefault())
        val d = NatalCompoundNext.nextAny(chart, 2, now)
        val t = NatalCompoundNext.nextAny(chart, 3, now)
        val doubleLine = d?.let {
            "2× ${NatalCompoundNext.labelKinds(it.kinds)} · ${fmt.format(it.at.atZone(zone))}"
        } ?: "No double in 2y"
        val tripleLine = t?.let {
            "3× ${NatalCompoundNext.labelKinds(it.kinds)} · ${fmt.format(it.at.atZone(zone))}"
        } ?: "No triple in 2y"
        val title = profile.label.ifBlank { "Chart" }
        return Triple(title, doubleLine, tripleLine)
    }
}
