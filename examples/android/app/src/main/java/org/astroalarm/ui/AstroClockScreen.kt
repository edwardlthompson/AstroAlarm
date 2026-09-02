package org.astroalarm.ui

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.foss.goldenpath.R
import kotlinx.coroutines.delay
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.settings.AstroDisplayPreferences
import org.astroalarm.astro.sun.SolarCalculator
import org.astroalarm.astro.zodiac.ZodiacCalculator
import org.astroalarm.widget.AstroClockWidgetProvider
import org.astroalarm.widget.AstroDiskRenderer
import org.astroalarm.widget.ClockRenderSize
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
    var now by remember { mutableStateOf(Instant.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            now = Instant.now()
        }
    }

    val zone = place?.zone ?: java.time.ZoneId.systemDefault()
    val nowZdt = ZonedDateTime.ofInstant(now, zone)
    val date = nowZdt.toLocalDate()
    val fmt = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    val solarNoon = place?.let { SolarCalculator.calculate(SolarEventType.SolarNoon, date, it.latitude, it.longitude, it.zone) }
    val solarMidnight = place?.let { SolarCalculator.calculate(SolarEventType.SolarMidnight, date, it.latitude, it.longitude, it.zone) }
    val middayZodiac = ZodiacCalculator.overheadMiddayZodiac(solarNoon ?: now)
    val midnightZodiac = ZodiacCalculator.overheadMidnightZodiac(solarMidnight ?: now)

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
            val sizePx = ClockRenderSize.fromMinDp(side.value.toInt().coerceAtLeast(80))
            val diskBitmap = remember(place, alarms, now.epochSecond / 10, sizePx, showZodiac, showEventTimes, showMonthTicks) {
                AstroDiskRenderer.renderDisk(place, alarms, now, sizePx, showZodiac, showEventTimes, showMonthTicks)
            }
            val zodiacHits = remember(place, now.epochSecond / 10, sizePx, showZodiac) {
                if (showZodiac) ZodiacRingLayout.diskHits(place, now, sizePx) else emptyList()
            }
            val uriHandler = LocalUriHandler.current
            Column(
                Modifier.fillMaxWidth().align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    bitmap = diskBitmap.asImageBitmap(),
                    contentDescription = stringResource(R.string.astro_widget_desc),
                    modifier = Modifier.size(side).pointerInput(zodiacHits, sizePx) {
                        detectTapGestures { tap ->
                            val bx = tap.x * sizePx / size.width
                            val by = tap.y * sizePx / size.height
                            ZodiacRingLayout.at(zodiacHits, bx, by)?.let { sign ->
                                runCatching { uriHandler.openUri(ZodiacRingLayout.wikipediaUrl(sign)) }
                            }
                        }
                    }
                )
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
            zodiacTitle = stringResource(R.string.astro_toggle_show_zodiac),
            eventTimesTitle = stringResource(R.string.astro_toggle_show_event_times),
            monthTicksTitle = stringResource(R.string.astro_toggle_show_month_ticks),
        )
    }
}

private fun pinClockWidget(context: android.content.Context) {
    val mgr = context.getSystemService(AppWidgetManager::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mgr != null && mgr.isRequestPinAppWidgetSupported) {
        mgr.requestPinAppWidget(ComponentName(context, AstroClockWidgetProvider::class.java), null, null)
        Toast.makeText(context, context.getString(R.string.astro_widget_pinned_success), Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, context.getString(R.string.astro_widget_pin_manual_guide), Toast.LENGTH_LONG).show()
    }
}
