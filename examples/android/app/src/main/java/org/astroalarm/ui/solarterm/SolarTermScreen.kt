package org.astroalarm.ui.solarterm

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.foss.goldenpath.R
import kotlinx.coroutines.delay
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.settings.AstroDisplayPreferences
import org.astroalarm.solarterm.SolarTerm
import org.astroalarm.ui.DiskChrome
import org.astroalarm.ui.OverlayToggleLine
import org.astroalarm.widget.EarthTexture
import org.astroalarm.widget.MoonTexture
import org.astroalarm.widget.SolarTermWidgetProvider
import java.time.Instant

@Composable
fun SolarTermScreen(
    place: AstroPlace?,
    alarms: List<AstroAlarm> = emptyList(),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val prefs = remember { AstroDisplayPreferences(context) }
    val compact by prefs.solarTermCompact.collectAsState()
    val showEventTimes by prefs.showEventTimesYearly.collectAsState()
    var now by remember { mutableStateOf(Instant.now()) }
    var scale by remember { mutableFloatStateOf(1f) }
    var selected by remember { mutableStateOf<SolarTerm?>(null) }
    val dark = isSystemInDarkTheme()
    val earth = remember { EarthTexture.get(context) }
    val moon = remember { MoonTexture.get(context) }
    val alarmOrds = remember(alarms, showEventTimes) {
        SolarTermAlarmDots.ordsOf(alarms, showEventTimes)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000L)
            now = Instant.now()
        }
    }

    val (snap, req) = remember(place, now.epochSecond / 60, dark, compact, alarmOrds) {
        SolarTermDrawFactory.request(context.resources, place, now, dark, compact, alarmOrds)
    }
    val talk = stringResource(R.string.solar_term_cd_wheel) + ". " +
        SolarTermFormat.nextGlance(context.resources, snap.next, SolarTermFormat.zoneOf(place), SolarTermFormat.southern(place))

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BoxWithConstraints(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.TopCenter) {
            val side = minOf(maxWidth, (maxHeight - DiskChrome.Reserve).coerceAtLeast(0.dp))
            val sizePx = org.astroalarm.widget.ClockRenderSize.fromMinDp(side.value.toInt().coerceAtLeast(80))
            val bmp = remember(req, sizePx, earth, moon) {
                SolarTermWheelRenderer.render(req, sizePx, earth, moon)
            }
            Column(
                Modifier.fillMaxWidth().align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = talk,
                    modifier = Modifier
                        .size(side)
                        .graphicsLayer { scaleX = scale; scaleY = scale }
                        .semantics { contentDescription = talk }
                        .pointerInput(sizePx, req.nowLon, compact) {
                            detectTapGestures { tap ->
                                val bx = tap.x * sizePx / size.width
                                val by = tap.y * sizePx / size.height
                                val idx = SolarTermWheelRenderer.sectorAt(bx, by, sizePx, req.nowLon, compact)
                                if (idx != null) selected = SolarTerm.entries[idx]
                            }
                        }
                        .pointerInput(Unit) {
                            detectTransformGestures { _, _, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 2.4f)
                            }
                        }
                )
                Button(
                    onClick = { pinWidget(context) },
                    modifier = Modifier.fillMaxWidth().semantics {
                        contentDescription = context.getString(R.string.solar_term_pin_widget_cd)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.solar_term_pin_widget))
                }
                Text(req.locationLabel, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text(req.todayLine, fontSize = 13.sp)
        Text(req.countdown, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
        ) {
            Column(Modifier.padding(horizontal = 12.dp, vertical = 2.dp)) {
                OverlayToggleLine(
                    stringResource(R.string.solar_term_toggle_compact),
                    compact,
                    { prefs.setSolarTermCompact(it) },
                )
                OverlayToggleLine(
                    stringResource(R.string.astro_toggle_show_event_times),
                    showEventTimes,
                    { prefs.setShowEventTimesYearly(it) },
                )
            }
        }
    }

    selected?.let { term ->
        val shown = term.localAlias(SolarTermFormat.southern(place))
        val occ = snap.year.occurrences.firstOrNull { it.term == term } ?: snap.current
        SolarTermDetailSheet(
            term = shown,
            english = SolarTermCopy.name(context.resources, shown),
            whenLocal = SolarTermFormat.localStamp(occ.utc, SolarTermFormat.zoneOf(place)),
            description = SolarTermCopy.desc(context.resources, shown),
            instant = occ.utc,
            zone = SolarTermFormat.zoneOf(place),
            onDismiss = { selected = null },
        )
    }
}

private fun pinWidget(context: android.content.Context) {
    val mgr = context.getSystemService(AppWidgetManager::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mgr != null && mgr.isRequestPinAppWidgetSupported) {
        mgr.requestPinAppWidget(ComponentName(context, SolarTermWidgetProvider::class.java), null, null)
        Toast.makeText(context, context.getString(R.string.astro_widget_pinned_success), Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, context.getString(R.string.astro_widget_pin_manual_guide), Toast.LENGTH_LONG).show()
    }
}
