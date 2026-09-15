package org.astroalarm.ui

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.foss.goldenpath.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.astroalarm.astro.sky.SkyBodies
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.settings.AstroDisplayPreferences
import org.astroalarm.astro.sun.SolarCalculator
import org.astroalarm.astro.zodiac.ZodiacCalculator
import org.astroalarm.share.SkyShareButton
import org.astroalarm.share.SkySharePaint
import org.astroalarm.ui.solarterm.WheelZoomPan
import org.astroalarm.ui.solarterm.WheelZoomPanMath
import org.astroalarm.widget.AstroClockWidgetProvider
import org.astroalarm.widget.AstroDiskRenderer
import org.astroalarm.widget.EarthTexture
import org.astroalarm.widget.MoonTexture
import org.astroalarm.widget.ZodiacRingLayout
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AstroClockScreen(
    place: AstroPlace?,
    alarms: List<AstroAlarm>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val displayPrefs = remember { AstroDisplayPreferences(context) }
    val showZodiac by displayPrefs.showZodiac2D.collectAsState()
    val showEventTimes by displayPrefs.showEventTimes2D.collectAsState()
    val showMonthTicks by displayPrefs.showMonthTicks2D.collectAsState()
    val showHourMarks by displayPrefs.showHourMarks2D.collectAsState()
    val earth = remember { EarthTexture.get(context) }
    val moon = remember { MoonTexture.get(context) }
    var now by remember { mutableStateOf(Instant.now()) }
    var viewport by remember { mutableStateOf(WheelZoomPan()) }
    val viewportLatest = rememberUpdatedState(viewport)

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            now = Instant.now()
        }
    }

    val haptic = LocalHapticFeedback.current
    var prevHa by remember { mutableFloatStateOf(Float.NaN) }
    LaunchedEffect(now, place) {
        val p = place ?: return@LaunchedEffect
        val ha = SkyBodies.sun(now, p.latitude, p.longitude)?.haRad ?: return@LaunchedEffect
        val deg = Math.toDegrees(ha).toFloat()
        if (!prevHa.isNaN() &&
            SkyHandTick.crossed(prevHa, deg, 0f) &&
            !ReduceMotion.enabled(context)
        ) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        prevHa = deg
    }

    val zone = place?.zone ?: java.time.ZoneId.systemDefault()
    val nowZdt = ZonedDateTime.ofInstant(now, zone)
    val date = nowZdt.toLocalDate()
    val fmt = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    val solarNoon = place?.let { SolarCalculator.calculate(SolarEventType.SolarNoon, date, it.latitude, it.longitude, it.zone) }
    val solarMidnight = place?.let { SolarCalculator.calculate(SolarEventType.SolarMidnight, date, it.latitude, it.longitude, it.zone) }
    val middayZodiac = ZodiacCalculator.overheadMiddayZodiac(solarNoon ?: now)
    val midnightZodiac = ZodiacCalculator.overheadMidnightZodiac(solarMidnight ?: now)
    val uriHandler = LocalUriHandler.current
    val shareScope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            val side = minOf(maxWidth, (maxHeight - DiskChrome.Reserve).coerceAtLeast(0.dp))
            val layoutPx = with(LocalDensity.current) { side.roundToPx() }.coerceAtLeast(80)
            Column(
                Modifier.fillMaxWidth().align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(Modifier.size(side)) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .pointerInput(Unit) {
                            detectTransformGestures { centroid, pan, zoom, _ ->
                                viewport = WheelZoomPanMath.apply(
                                    viewportLatest.value,
                                    centroid.x, centroid.y, pan.x, pan.y, zoom,
                                    size.width.toFloat(), size.height.toFloat(),
                                )
                            }
                        }
                        .pointerInput(layoutPx, place, now.epochSecond / 10, showZodiac, showMonthTicks) {
                            detectTapGestures { tap ->
                                if (!showZodiac) return@detectTapGestures
                                val (lx, ly) = WheelZoomPanMath.contentPoint(
                                    tap.x, tap.y, viewportLatest.value,
                                    size.width.toFloat(), size.height.toFloat(),
                                )
                                val bx = lx * layoutPx / size.width
                                val by = ly * layoutPx / size.height
                                val hits = ZodiacRingLayout.diskHits(place, now, layoutPx, showMonthTicks)
                                ZodiacRingLayout.at(hits, bx, by)?.let { sign ->
                                    runCatching { uriHandler.openUri(ZodiacRingLayout.wikipediaUrl(sign)) }
                                }
                            }
                        }
                        .wheelTalkBack(
                            description = context.getString(R.string.astro_widget_desc),
                            nextLabel = stringResource(R.string.a11y_next_event),
                            resetLabel = stringResource(R.string.a11y_reset_zoom),
                            shareLabel = stringResource(R.string.sky_share_cd),
                            onNext = { NextEventA11y.announce(context, alarms, place) },
                            onReset = { viewport = WheelZoomPan(); true },
                            onShare = {
                                val v = viewport
                                val z = showZodiac
                                val et = showEventTimes
                                val mt = showMonthTicks
                                val hm = showHourMarks
                                val t = now
                                shareScope.launch {
                                    org.astroalarm.share.sharePaintedSky(
                                        context,
                                        { px ->
                                            SkySharePaint.daily2d(
                                                px, place, alarms, t, v, z, et, mt, hm, earth, moon,
                                            )
                                        },
                                        context.getString(R.string.sky_share_chooser),
                                        context.getString(R.string.sky_share_failed),
                                        context.getString(R.string.sky_share_oom),
                                    )
                                }
                                true
                            },
                        )
                ) {
                    val px = size.width.toInt().coerceAtLeast(1)
                    drawIntoCanvas { gc ->
                        val native = gc.nativeCanvas
                        native.save()
                        WheelZoomPanMath.concat(native, viewport, size.width)
                        AstroDiskRenderer.draw(
                            native, place, alarms, now, px,
                            showZodiac, showEventTimes, showMonthTicks, showHourMarks, earth, moon,
                        )
                        native.restore()
                    }
                }
                SkyShareButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    paint = {
                        val v = viewport
                        val z = showZodiac
                        val et = showEventTimes
                        val mt = showMonthTicks
                        val hm = showHourMarks
                        val t = now
                        { px ->
                            SkySharePaint.daily2d(
                                px, place, alarms, t, v, z, et, mt, hm, earth, moon,
                            )
                        }
                    },
                )
                }
                Button(
                    onClick = { pinClockWidget(context) },
                    modifier = Modifier.fillMaxWidth().semantics {
                        contentDescription = context.getString(R.string.astro_add_widget_cd)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.astro_add_widget_btn))
                }
                Text(
                    text = "☀️ ${solarNoon?.let { ZonedDateTime.ofInstant(it, zone).format(fmt) } ?: "--:--"}   " +
                        "${middayZodiac.symbol} ${middayZodiac.englishName}   " +
                        "🌙 ${solarMidnight?.let { ZonedDateTime.ofInstant(it, zone).format(fmt) } ?: "--:--"}   " +
                        "${midnightZodiac.symbol} ${midnightZodiac.englishName}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2
                )
            }
        }

        ClockOverlayToggles(
            showZodiac = showZodiac,
            onShowZodiacChange = { displayPrefs.setShowZodiac2D(it) },
            showEventTimes = showEventTimes,
            onShowEventTimesChange = { displayPrefs.setShowEventTimes2D(it) },
            showMonthTicks = showMonthTicks,
            onShowMonthTicksChange = { displayPrefs.setShowMonthTicks2D(it) },
            showHourMarks = showHourMarks,
            onShowHourMarksChange = { displayPrefs.setShowHourMarks2D(it) },
            zodiacTitle = stringResource(R.string.astro_toggle_show_zodiac),
            eventTimesTitle = stringResource(R.string.astro_toggle_show_event_times),
            monthTicksTitle = stringResource(R.string.astro_toggle_show_month_ticks),
            hourMarksTitle = stringResource(R.string.astro_toggle_show_hour_marks),
        )
    }
}

private fun pinClockWidget(context: android.content.Context) {
    org.astroalarm.widget.WidgetPin.request(context, AstroClockWidgetProvider::class.java)
}
