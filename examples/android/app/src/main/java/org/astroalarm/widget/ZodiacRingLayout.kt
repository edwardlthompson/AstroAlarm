package org.astroalarm.widget

import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.sun.SolarCalculator
import org.astroalarm.astro.zodiac.ZodiacCalculator
import org.astroalarm.astro.zodiac.ZodiacSign
import java.time.Instant
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/** 2D zodiac bubble positions, hit-testing, and Wikipedia URLs. */
object ZodiacRingLayout {
    data class Hit(val sign: ZodiacSign, val x: Float, val y: Float, val r: Float)

    fun wikipediaUrl(sign: ZodiacSign): String =
        "https://en.wikipedia.org/wiki/${sign.englishName}_(astrology)"

    fun cuspAngles(sunLon: Double, aNoonDeg: Float): List<Float> =
        ZodiacSign.entries.map { aNoonDeg + (it.startLongitudeDeg - sunLon).toFloat() }

    fun positions(center: Float, dist: Float, sunLon: Double, aNoonDeg: Float, size: Int): List<Hit> {
        val r = ZodiacGlyph.radius(size)
        return ZodiacSign.entries.map { sign ->
            val deg = aNoonDeg + (sign.startLongitudeDeg + 15.0 - sunLon).toFloat()
            val rad = deg * (Math.PI / 180.0)
            Hit(sign, center + dist * cos(rad).toFloat(), center + dist * sin(rad).toFloat(), r)
        }
    }

    fun diskHits(place: AstroPlace?, now: Instant, size: Int): List<Hit> {
        val center = size / 2f
        val dist = ZodiacGlyph.ringDistance(center, size)
        val zone = place?.zone ?: java.time.ZoneId.systemDefault()
        val nowZdt = now.atZone(zone)
        val nowAngle = (nowZdt.hour * 60 + nowZdt.minute + nowZdt.second / 60f) / 1440f * 360f
        val noon = place?.let {
            SolarCalculator.calculate(SolarEventType.SolarNoon, nowZdt.toLocalDate(), it.latitude, it.longitude, it.zone)
        }
        val aNoonDeg = noon?.let {
            val z = it.atZone(zone)
            ((z.hour * 60 + z.minute + z.second / 60f) / 1440f) * 360f - nowAngle - 90f
        } ?: -90f
        return positions(center, dist, ZodiacCalculator.sunLongitudeAt(noon ?: now), aNoonDeg, size)
    }

    fun at(hits: List<Hit>, x: Float, y: Float): ZodiacSign? =
        hits.minByOrNull { hypot((it.x - x).toDouble(), (it.y - y).toDouble()) }
            ?.takeIf { hypot((it.x - x).toDouble(), (it.y - y).toDouble()) <= it.r * 1.2 }
            ?.sign
}
