package org.astroalarm.astro

import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.sun.SolarCalculator
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.abs

/**
 * Compare on-device SolarCalculator output to a published NOAA almanac table (30 rows)
 * and write docs/ACCURACY.md pass/fail results.
 */
class EphemerisAccuracyTest {
    data class Row(
        val date: LocalDate,
        val lat: Double,
        val lon: Double,
        val zone: ZoneId,
        val event: String,
        val expected: Instant,
    )

    @Test
    fun compareAlmanacAndWriteReport() {
        val (tol, rows) = loadCsv()
        val lines = mutableListOf(
            "# Ephemeris accuracy",
            "",
            "On-device commons-suncalc vs published NOAA Solar Calculator equations",
            "(https://gml.noaa.gov/grad/solcalc/). Tolerance ±${tol}s.",
            "",
            "| Date | Event | Almanac (UTC) | App (UTC) | Δs | Result |",
            "|------|-------|---------------|-----------|----:|--------|",
        )
        var failures = 0
        for (row in rows) {
            val type = if (row.event == "sunrise") SolarEventType.Sunrise else SolarEventType.Sunset
            val actual = SolarCalculator.calculate(type, row.date, row.lat, row.lon, row.zone)
            val delta = if (actual == null) Long.MAX_VALUE else abs(ChronoUnit.SECONDS.between(row.expected, actual))
            val pass = actual != null && delta <= tol
            if (!pass) failures++
            lines += "| ${row.date} | ${row.event} | ${row.expected} | " +
                "${actual ?: "null"} | ${if (delta == Long.MAX_VALUE) "—" else delta} | " +
                "${if (pass) "PASS" else "FAIL"} |"
        }
        lines += ""
        lines += "**Summary:** ${rows.size - failures}/${rows.size} passed (tolerance ±${tol}s)."
        lines += ""
        val out = repoDocsAccuracy()
        Files.createDirectories(out.parent)
        Files.writeString(out, lines.joinToString("\n") + "\n")
        assertTrue("Almanac accuracy failures=$failures — see docs/ACCURACY.md", failures == 0)
    }

    private fun loadCsv(): Pair<Long, List<Row>> {
        val text = javaClass.classLoader!!
            .getResourceAsStream("almanac_reference.csv")!!
            .bufferedReader()
            .readLines()
            .filter { it.isNotBlank() }
        val tol = text[0].substringAfter(',').trim().toLong()
        val rows = text.drop(2).map { line ->
            val p = line.split(',')
            Row(
                date = LocalDate.parse(p[0]),
                lat = p[1].toDouble(),
                lon = p[2].toDouble(),
                zone = ZoneId.of(p[3]),
                event = p[4],
                expected = Instant.parse(p[5]),
            )
        }
        return tol to rows
    }

    private fun repoDocsAccuracy(): Path {
        var dir = Path.of("").toAbsolutePath()
        repeat(8) {
            if (Files.isDirectory(dir.resolve("examples"))) {
                return dir.resolve("docs").resolve("ACCURACY.md")
            }
            dir = dir.parent ?: return Path.of("docs", "ACCURACY.md")
        }
        return Path.of("docs", "ACCURACY.md")
    }
}
